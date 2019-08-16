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

package com.dinstone.photon.transport.client;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLEngine;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.handler.HandlerManager;
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.session.DefaultSession;
import com.dinstone.photon.session.Session;
import com.dinstone.photon.transport.TransportConfig;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Connector {

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    private final NioEventLoopGroup workGroup;

    private final Bootstrap clientBoot;

    private int refCount;

    private MessageProcessor messageProcessor;

    public Connector(final TransportConfig transportConfig) {

        workGroup = new NioEventLoopGroup(transportConfig.getConnectPoolSize(), new DefaultThreadFactory("N4C-Work"));
        clientBoot = new Bootstrap().group(workGroup).channel(NioSocketChannel.class);
        clientBoot.option(ChannelOption.TCP_NODELAY, true);
        clientBoot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, transportConfig.getConnectTimeout());
        clientBoot.option(ChannelOption.SO_RCVBUF, 8 * 1024).option(ChannelOption.SO_SNDBUF, 8 * 1024);
        clientBoot.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (transportConfig.enableCrypt()) {
                    SSLEngine engine = createSslEngine(ch.alloc());
                    ch.pipeline().addFirst(new SslHandler(engine));
                }

                ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
                ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

                ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(60, 30, 0));
                ch.pipeline().addLast("ClientHandler", new ClientHandler());
            }
        });
    }

    protected SSLEngine createSslEngine(ByteBufAllocator byteBufAllocator) throws Exception {
        SslContextBuilder builder = SslContextBuilder.forClient();
        builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        return builder.build().newEngine(byteBufAllocator);
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
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

    public void dispose() {
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }

    public Session createSession(InetSocketAddress sa) throws Exception {
        // connect to peer
        ChannelFuture channelFuture = clientBoot.connect(sa).awaitUninterruptibly();
        if (!channelFuture.isSuccess()) {
            throw new RuntimeException(channelFuture.cause());
        }

        Channel channel = channelFuture.channel();
        // create session
        DefaultSession session = new DefaultSession(channel);
        AttributeHelper.setSession(channel, session);

        LOG.debug("session connect {} to {}", channel.localAddress(), channel.remoteAddress());
        return session;
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
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            LOG.info("client received message : {}", msg);

            MessageHandler<Object> messageHandler = HandlerManager.find(msg.getClass());
            if (messageHandler != null) {
                messageHandler.handle(new MessageContext(ctx, messageProcessor), msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
            LOG.error("Unhandled Exception", cause);
            ctx.close();
        }

    }
}
