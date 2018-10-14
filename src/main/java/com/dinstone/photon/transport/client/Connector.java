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

import com.dinstone.photon.ArrayUtil;
import com.dinstone.photon.AttributeHelper;
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
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;

public class Connector {

	private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

	private final NioEventLoopGroup workGroup;

	private final Bootstrap clientBoot;

	private final KeyPair keyPair;

	private int refCount;

	public Connector(final TransportConfig transportConfig) {
		if (transportConfig.enableCrypt()) {
			try {
				this.keyPair = RsaCrypto.generateKeyPair();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			this.keyPair = null;
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

	public Session createSession(InetSocketAddress sa) throws Exception {
		ChannelFuture channelFuture = clientBoot.connect(sa).awaitUninterruptibly();
		if (!channelFuture.isSuccess()) {
			throw new RuntimeException(channelFuture.cause());
		}

		Channel channel = channelFuture.channel();

		Future<Void> connectFuture = AttributeHelper.getConnectPromise(channel);
		if (!connectFuture.await().isSuccess()) {
			throw new RuntimeException(connectFuture.cause());
		}

		DefaultSession session = new DefaultSession(channel);
		AttributeHelper.setSession(channel, session);

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
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			AttributeHelper.setConnectPromise(ctx.channel(), new DefaultPromise<Void>(GlobalEventExecutor.INSTANCE));
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			if (keyPair != null) {
				byte[] saltBytes = AesCrypto.genAesSalt();
				byte[] publicKey = keyPair.getPublic().getEncoded();
				ctx.channel().writeAndFlush(new Agreement(ArrayUtil.concat(saltBytes, publicKey)));
			} else {
				AttributeHelper.getConnectPromise(ctx.channel()).trySuccess(null);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			LOG.info("client received message : {}", msg);
			if (msg instanceof Agreement) {
				byte[] encoded = keyPair.getPrivate().getEncoded();
				byte[] aeskey = new PrivateKeyCipher(encoded).decrypt(((Agreement) msg).getData());
				AttributeHelper.setCipher(ctx.channel(), new AesCrypto(aeskey));

				AttributeHelper.getConnectPromise(ctx.channel()).trySuccess(null);
			}
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			Promise<Void> connectPromise = AttributeHelper.getConnectPromise(ctx.channel());
			if (!connectPromise.isDone()) {
				connectPromise.tryFailure(new ConnectException("connection is closed"));
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
			LOG.error("Unhandled Exception", cause);
			ctx.close();
		}

	}
}
