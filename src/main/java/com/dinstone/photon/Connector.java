/*
 * Copyright (C) 2013~2017 dinstone<dinstone@163.com>
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
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Connector {

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    private final NioEventLoopGroup workGroup;

    private final Bootstrap bootstrap;

    private ConnectOptions options;

    private int refCount;

    private ExecutorService executorService;

    private MessageProcessor messageProcessor;

    public Connector(final ConnectOptions connectOptions) {
        this.options = connectOptions;

        workGroup = new NioEventLoopGroup(options.getEventLoopSize(), new DefaultThreadFactory("N4C-Work"));
        bootstrap = new Bootstrap().group(workGroup).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (options.isEnableSsl()) {
                    SSLEngine engine = createSslEngine(ch.alloc());
                    ch.pipeline().addFirst(new SslHandler(engine));
                }

                ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
                ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

                ch.pipeline().addLast("IdleStateHandler",
                        new IdleStateHandler(2 * options.getIdleTimeout(), options.getIdleTimeout(), 0));
                ch.pipeline().addLast("ClientHandler", new ClientHandler());
            }
        });
        applyConnectionOptions(bootstrap);

        int processorSize = options.getProcessorSize();
        if (processorSize > 0) {
            NamedThreadFactory threadFactory = new NamedThreadFactory("N4A-Processor");
            executorService = Executors.newFixedThreadPool(processorSize, threadFactory);
        }
    }

    private void applyConnectionOptions(Bootstrap bootstrap) {
        bootstrap.option(ChannelOption.SO_REUSEADDR, options.isReuseAddress());
        bootstrap.option(ChannelOption.TCP_NODELAY, options.isTcpNoDelay());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, options.isTcpKeepAlive());

        if (options.getLocalAddress() != null) {
            bootstrap.localAddress(options.getLocalAddress(), 0);
        }
        if (options.getSendBufferSize() != -1) {
            bootstrap.option(ChannelOption.SO_SNDBUF, options.getSendBufferSize());
        }
        if (options.getReceiveBufferSize() != -1) {
            bootstrap.option(ChannelOption.SO_RCVBUF, options.getReceiveBufferSize());
            bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,
                    new FixedRecvByteBufAllocator(options.getReceiveBufferSize()));
        }
        if (options.getSoLinger() != -1) {
            bootstrap.option(ChannelOption.SO_LINGER, options.getSoLinger());
        }
        if (options.getTrafficClass() != -1) {
            bootstrap.option(ChannelOption.IP_TOS, options.getTrafficClass());
        }
        if (options.isUsePooledBuffers()) {
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnectTimeout());
    }

    protected SSLEngine createSslEngine(ByteBufAllocator byteBufAllocator) throws Exception {
        SslContextBuilder builder = SslContextBuilder.forClient();
        builder.trustManager(options.getTrustManagerFactory());
        return builder.build().newEngine(byteBufAllocator);
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        if (messageProcessor == null) {
            throw new IllegalArgumentException("messageProcessor is null");
        }
        this.messageProcessor = messageProcessor;
    }

    /**
    *
    */
    public void incrementRefCount() {
        ++refCount;
    }

    /**
    *
    */
    public void decrementRefCount() {
        if (refCount > 0) {
            --refCount;
        }
    }

    /**
     * @return
     */
    public boolean isZeroRefCount() {
        return refCount == 0;
    }

    public void destroy() {
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        if (executorService != null) {
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
    }

    public Connection connect(SocketAddress sa) throws Exception {
        checkMessageProcessor();

        // connect to peer
        ChannelFuture channelFuture = bootstrap.connect(sa).awaitUninterruptibly();
        if (!channelFuture.isSuccess()) {
            throw new RuntimeException(channelFuture.cause());
        }

        Channel channel = channelFuture.channel();
        DefaultConnection connection = new DefaultConnection(channel);
        ConnectionManager.addConnection(channel, connection);
        AttributeHelper.setConnection(channel, connection);

        LOG.debug("session connect {} to {}", channel.localAddress(), channel.remoteAddress());
        return connection;
    }

    private void checkMessageProcessor() {
        if (messageProcessor == null) {
            throw new IllegalStateException("messageProcessor not set");
        }
    }

    public class ClientHandler extends ChannelInboundHandlerAdapter {

        private Heartbeat heartbeat = new Heartbeat(0, true);

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.READER_IDLE) {
                    ctx.close();
                } else if (event.state() == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(heartbeat);
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ConnectionManager.delConnection(ctx.channel());
            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            LOG.info("client received message : {}", msg);

            MessageHandler<Object> messageHandler = HandlerManager.find(msg.getClass());
            if (messageHandler != null) {
                messageHandler.handle(new MessageContext(ctx, executorService), messageProcessor, msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
            LOG.error("Unhandled Exception", cause);
            ctx.close();
        }

    }
}
