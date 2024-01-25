/*
 * Copyright (C) 2018~2024 dinstone<dinstone@163.com>
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

import java.net.ConnectException;
import java.net.SocketAddress;

import javax.net.ssl.SSLEngine;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.codec.MessageDecoder;
import com.dinstone.photon.codec.MessageEncoder;
import com.dinstone.photon.connection.CancelledConnectException;
import com.dinstone.photon.connection.ConnectionManager;
import com.dinstone.photon.connection.DefaultConnection;
import com.dinstone.photon.connection.TimeoutConnectException;
import com.dinstone.photon.connection.WrappedConnectException;
import com.dinstone.photon.handler.Dispatcher;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.utils.AttributeUtil;

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
import io.netty.util.concurrent.Future;

public class Connector {

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    private final NioEventLoopGroup workGroup;

    private final ConnectOptions options;

    private final Bootstrap bootstrap;

    private Dispatcher dispatcher;

    public Connector(ConnectOptions connectOptions) {
        this.options = connectOptions;

        workGroup = new NioEventLoopGroup(options.getWorkerSize(), new DefaultThreadFactory("PCT-Worker"));
        bootstrap = new Bootstrap().group(workGroup).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (options.isEnableSsl()) {
                    SSLEngine engine = createSslEngine(ch.alloc());
                    ch.pipeline().addFirst(new SslHandler(engine));
                }

                ch.pipeline().addLast("MessageDecoder", new MessageDecoder());
                ch.pipeline().addLast("MessageEncoder", new MessageEncoder());

                ch.pipeline().addLast("IdleStateHandler",
                        new IdleStateHandler(2 * options.getIdleTimeout(), options.getIdleTimeout(), 0));
                ch.pipeline().addLast("ClientHandler", new ClientHandler());
            }
        });
        applyNetworkOptions(bootstrap, connectOptions);
    }

    private void applyNetworkOptions(Bootstrap bootstrap, ConnectOptions options) {
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

    private SSLEngine createSslEngine(ByteBufAllocator byteBufAllocator) throws Exception {
        SslContextBuilder builder = SslContextBuilder.forClient();
        builder.trustManager(options.getTrustManagerFactory());
        return builder.build().newEngine(byteBufAllocator);
    }

    public Connector setMessageProcessor(MessageProcessor messageProcessor) {
        if (messageProcessor == null) {
            throw new IllegalArgumentException("message processor is null");
        }
        this.dispatcher = new Dispatcher(messageProcessor);
        return this;
    }

    public Future<?> destroy() {
        return workGroup.shutdownGracefully();
    }

    public Connection connect(SocketAddress sa) throws Exception {
        checkMessageProcessor();

        // wait connect to peer
        ChannelFuture channelFuture = bootstrap.connect(sa).awaitUninterruptibly();

        if (!channelFuture.isDone()) {
            throw new TimeoutConnectException(sa);
        }

        if (channelFuture.isCancelled()) {
            throw new CancelledConnectException(sa);
        }

        if (!channelFuture.isSuccess()) {
            if (channelFuture.cause() instanceof ConnectException) {
                throw (ConnectException) channelFuture.cause();
            } else {
                throw new WrappedConnectException(sa, channelFuture.cause());
            }
        }

        Channel channel = channelFuture.channel();
        Connection connection = new DefaultConnection(channel);
        ConnectionManager.addConnection(channel, connection);
        AttributeUtil.connection(channel, connection);

        LOG.debug("connection created from {} to {}", channel.localAddress(), channel.remoteAddress());
        return connection;
    }

    private void checkMessageProcessor() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher(new MessageProcessor());
        }
    }

    private class ClientHandler extends ChannelInboundHandlerAdapter {

        private final Heartbeat heartbeat = new Heartbeat();

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.READER_IDLE) {
                    ctx.close();
                } else if (event.state() == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(heartbeat.ping());
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
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            dispatcher.dispatch(ctx, msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("Unhandled Exception", cause);
            ctx.close();
        }

    }
}
