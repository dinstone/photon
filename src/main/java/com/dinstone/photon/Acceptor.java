/*
 * Copyright (C) 2018~2020 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.photon;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.connection.ConnectionManager;
import com.dinstone.photon.connection.DefaultConnection;
import com.dinstone.photon.handler.HandlerManager;
import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;
import com.dinstone.photon.util.AttributeHelper;
import com.dinstone.photon.util.NamedThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Acceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Acceptor.class);

    private AcceptOptions options;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    private ServerBootstrap bootstrap;

    private ExecutorService executorService;

    private MessageProcessor messageProcessor;

    public Acceptor(AcceptOptions acceptOptions) {
        this.options = acceptOptions;

        bossGroup = new NioEventLoopGroup(options.getAcceptSize(), new DefaultThreadFactory("PAT-Boss"));
        workGroup = new NioEventLoopGroup(options.getWorkerSize(), new DefaultThreadFactory("PAT-Work"));
        bootstrap = new ServerBootstrap().group(bossGroup, workGroup);
        bootstrap.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (options.isEnableSsl()) {
                    SSLEngine engine = createSslEngine(ch.alloc());
                    ch.pipeline().addFirst(new SslHandler(engine));
                }
                ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
                ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

                ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(2 * options.getIdleTimeout(), 0, 0));
                ch.pipeline().addLast("ServerHandler", new ServerHandler());
            }
        });
        applyConnectionOptions(bootstrap, acceptOptions);

        int processorSize = options.getProcessorSize();
        if (processorSize > 0) {
            NamedThreadFactory threadFactory = new NamedThreadFactory("PBT-Processor");
            executorService = Executors.newFixedThreadPool(processorSize, threadFactory);
        }
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        if (messageProcessor == null) {
            throw new IllegalArgumentException("messageProcessor is null");
        }
        this.messageProcessor = messageProcessor;
    }

    public Acceptor bind(SocketAddress sa) {
        checkMessageProcessor();

        try {
            bootstrap.bind(sa).sync();
        } catch (Exception e) {
            throw new RuntimeException("can't bind service on " + sa, e);
        }
        LOG.info("photon acceptor bind on {}", sa);

        return this;
    }

    private void applyConnectionOptions(ServerBootstrap bootstrap, AcceptOptions options) {
        if (options.getSoLinger() != -1) {
            bootstrap.option(ChannelOption.SO_LINGER, options.getSoLinger());
        }
        bootstrap.option(ChannelOption.SO_REUSEADDR, options.isReuseAddress());
        if (options.getAcceptBacklog() != -1) {
            bootstrap.option(ChannelOption.SO_BACKLOG, options.getAcceptBacklog());
        }

        bootstrap.childOption(ChannelOption.TCP_NODELAY, options.isTcpNoDelay());
        if (options.getSendBufferSize() != -1) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, options.getSendBufferSize());
        }
        if (options.getReceiveBufferSize() != -1) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, options.getReceiveBufferSize());
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR,
                    new FixedRecvByteBufAllocator(options.getReceiveBufferSize()));
        }
        if (options.getTrafficClass() != -1) {
            bootstrap.childOption(ChannelOption.IP_TOS, options.getTrafficClass());
        }
        if (options.isUsePooledBuffers()) {
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            bootstrap.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, options.isTcpKeepAlive());
    }

    private void checkMessageProcessor() {
        if (messageProcessor == null) {
            throw new IllegalStateException("messageProcessor not set");
        }
    }

    private SSLEngine createSslEngine(ByteBufAllocator byteBufAllocator) throws Exception {
        SslContextBuilder builder = SslContextBuilder.forServer(options.getPrivateKey(), options.getCertChain());
        SSLEngine sslEngine = builder.build().newEngine(byteBufAllocator);
        sslEngine.setUseClientMode(false);
        return sslEngine;
    }

    public void destroy() {
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (executorService != null) {
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
    }

    private class ServerHandler extends ChannelInboundHandlerAdapter {

        private int connectionLimit;

        public ServerHandler() {
            connectionLimit = options.getConnectionLimit();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.READER_IDLE) {
                    ctx.close();
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            int connectionCount = ConnectionManager.connectionCount();
            if (connectionLimit > 0 && connectionCount >= connectionLimit) {
                ctx.close();
                LOG.warn("connection count is more than limit: limit={},count={}", connectionLimit, connectionCount);
            } else {
                Connection connection = new DefaultConnection(ctx.channel());
                ConnectionManager.addConnection(ctx.channel(), connection);
                AttributeHelper.setConnection(ctx.channel(), connection);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ConnectionManager.delConnection(ctx.channel());

            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            MessageHandler<Object> messageHandler = HandlerManager.find(msg.getClass());
            if (messageHandler != null) {
                messageHandler.handle(executorService, messageProcessor, ctx, msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("untreated exception", cause);
            ctx.close();
        }
    }
}
