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

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.security.KeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.photon.AttributeKeys;
import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.crypto.AesCrypto;
import com.dinstone.photon.crypto.RsaCrypto;
import com.dinstone.photon.crypto.RsaCrypto.PrivateKeyCipher;
import com.dinstone.photon.protocol.Agreement;
import com.dinstone.photon.protocol.Heartbeat;
import com.dinstone.photon.session.DefaultSession;
import com.dinstone.photon.session.Session;
import com.dinstone.photon.transport.MessageDecoder;
import com.dinstone.photon.transport.MessageEncoder;
import com.dinstone.photon.transport.TransportConfig;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;

public class Connector {

	private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

	private NioEventLoopGroup workGroup;

	private Bootstrap clientBoot;

	private KeyPair keyPair;

	private int refCount;

	public Connector(final TransportConfig transportConfig) {
		if (transportConfig.enableCrypt()) {
			try {
				this.keyPair = RsaCrypto.generateKeyPair();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		workGroup = new NioEventLoopGroup(transportConfig.getConnectPoolSize(), new DefaultThreadFactory("N4C-Work"));
		clientBoot = new Bootstrap().group(workGroup).channel(NioSocketChannel.class);
		clientBoot.option(ChannelOption.TCP_NODELAY, true);
		clientBoot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, transportConfig.getConnectTimeout());
		clientBoot.option(ChannelOption.SO_RCVBUF, 8 * 1024).option(ChannelOption.SO_SNDBUF, 8 * 1024);
		clientBoot.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
				ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

				ch.pipeline().addLast("MessageDecoder", new MessageDecoder(CodecManager.getInstance()));
				ch.pipeline().addLast("MessageEncoder", new MessageEncoder(CodecManager.getInstance()));

				ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(60, 30, 0));
				ch.pipeline().addLast("NettyClientHandler", new NettyClientHandler());
			}
		});
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

	// public Channel createChannel(InetSocketAddress sa) {
	// Channel channel = clientBoot.connect(sa).awaitUninterruptibly().channel();
	// LOG.debug("session connect {} to {}", channel.localAddress(),
	// channel.remoteAddress());
	// return channel;
	// }

	public Session createSession(InetSocketAddress sa) throws Exception {
		ChannelFuture channelFuture = clientBoot.connect(sa).awaitUninterruptibly();
		if (!channelFuture.isSuccess()) {
			throw new RuntimeException(channelFuture.cause());
		}

		Channel channel = channelFuture.channel();
		if (keyPair != null) {
			Attribute<Promise<Agreement>> promiseAttr = channel.attr(AttributeKeys.PROMISE_KEY);
			promiseAttr.set(new DefaultPromise<Agreement>(GlobalEventExecutor.INSTANCE));

			channel.writeAndFlush(new Agreement(keyPair.getPublic().getEncoded()));

			Future<Agreement> future = promiseAttr.get().awaitUninterruptibly();
			if (!future.isSuccess()) {
				throw new RuntimeException(future.cause());
			}

			promiseAttr.set(null);

			byte[] encoded = keyPair.getPrivate().getEncoded();
			byte[] aesKey = new PrivateKeyCipher(encoded).decrypt(future.get().getData());
			channel.attr(AttributeKeys.CIPHER_KEY).set(new AesCrypto.KeyCipher(aesKey));
		}

		DefaultSession session = new DefaultSession(channel);
		channel.attr(AttributeKeys.SESSION_KEY).set(session);

		LOG.debug("session connect {} to {}", channel.localAddress(), channel.remoteAddress());
		return session;
	}

	public class NettyClientHandler extends ChannelInboundHandlerAdapter {

		private Heartbeat heartbeat = new Heartbeat(0);

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent event = (IdleStateEvent) evt;
				if (event.state() == IdleState.READER_IDLE) {
					ctx.close();
				} else if (event.state() == IdleState.WRITER_IDLE) {
					heartbeat.increase();
					ctx.writeAndFlush(heartbeat);
				}
			} else {
				super.userEventTriggered(ctx, evt);
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			// DefaultSession session = new DefaultSession();
			// ctx.channel().attr(Session.SESSION_KEY).set(session);
			//
			// if (keyPair != null) {
			// ctx.writeAndFlush(new Crypt(keyPair.getPublic().getEncoded()));
			// }
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			AttributeKey<Promise<Agreement>> akey = AttributeKey.valueOf("crypt.promise.key");
			Promise<Agreement> promise = ctx.channel().attr(akey).get();
			if (promise != null) {
				promise.tryFailure(new ConnectException("connection is closed"));
			}

			super.channelInactive(ctx);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

			LOG.info("client received message : {}", msg);
			if (msg instanceof Agreement) {
				ctx.channel().attr(AttributeKeys.PROMISE_KEY).get().trySuccess((Agreement) msg);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
			LOG.error("Unhandled Exception", cause);
			ctx.close();
		}

	}
}
