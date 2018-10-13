package com.dinstone.photon.transport.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.photon.ArrayUtil;
import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.crypto.AesCrypto;
import com.dinstone.photon.crypto.RsaCrypto;
import com.dinstone.photon.crypto.RsaCrypto.PublicKeyCipher;
import com.dinstone.photon.protocol.Agreement;
import com.dinstone.photon.protocol.Heartbeat;
import com.dinstone.photon.session.DefaultSession;
import com.dinstone.photon.transport.MessageDecoder;
import com.dinstone.photon.transport.MessageEncoder;
import com.dinstone.photon.transport.NetworkInterfaceUtil;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Acceptor {

	private static final Logger LOG = LoggerFactory.getLogger(Acceptor.class);

	private static final AttributeKey<String> LOCAL_REMOTE_ADDRESS_KEY = AttributeKey
			.valueOf("local-remote-address-key");

	private final ConcurrentMap<String, Channel> connectionMap = new ConcurrentHashMap<>();

	private EventLoopGroup bossGroup;

	private EventLoopGroup workGroup;

	private ExecutorService executorService;

	public Acceptor bind() {
		bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("N4A-Boss"));
		workGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("N4A-Work"));

		ServerBootstrap boot = new ServerBootstrap().group(bossGroup, workGroup);
		boot.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
				ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

				ch.pipeline().addLast("MessageDecoder", new MessageDecoder(CodecManager.getInstance()));
				ch.pipeline().addLast("MessageEncoder", new MessageEncoder(CodecManager.getInstance()));

				ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(60, 0, 0));
				ch.pipeline().addLast("NettyServerHandler", new NettyServerHandler());
			}
		});
		boot.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_BACKLOG, 128);
		boot.childOption(ChannelOption.SO_RCVBUF, 4 * 1024).childOption(ChannelOption.SO_SNDBUF, 4 * 1024)
				.childOption(ChannelOption.TCP_NODELAY, true);

		InetSocketAddress serviceAddress = new InetSocketAddress("127.0.0.1", 4444);
		try {
			boot.bind(serviceAddress).sync();

			// int processorCount = transportConfig.getBusinessProcessorCount();
			// if (processorCount > 0) {
			// NamedThreadFactory threadFactory = new
			// NamedThreadFactory("N4A-BusinessProcessor");
			// executorService = Executors.newFixedThreadPool(processorCount,
			// threadFactory);
			// }
		} catch (Exception e) {
			throw new RuntimeException("can't bind service on " + serviceAddress, e);
		}
		LOG.info("netty acceptance bind on {}", serviceAddress);

		return this;
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

	private class NettyServerHandler extends ChannelInboundHandlerAdapter {

		private final int maxConnectionCount = 4;

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
			int currentConnectioncount = connectionMap.size();
			if (currentConnectioncount >= maxConnectionCount) {
				ctx.close();
				LOG.warn("connection count is too big: limit={},current={}", maxConnectionCount,
						currentConnectioncount);
			} else {
				Channel channel = ctx.channel();
				String addressLabel = NetworkInterfaceUtil.addressLabel(channel.remoteAddress(),
						channel.localAddress());
				channel.attr(LOCAL_REMOTE_ADDRESS_KEY).set(addressLabel);
				connectionMap.put(addressLabel, channel);

				DefaultSession session = new DefaultSession();
				AttributeHelper.setSession(ctx.channel(), session);
			}
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			String connectionKey = ctx.channel().attr(LOCAL_REMOTE_ADDRESS_KEY).get();
			if (connectionKey != null) {
				connectionMap.remove(connectionKey);
			}

			super.channelInactive(ctx);
		}

		@Override
		public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

			LOG.info("server received message : {}", msg);

			if (msg instanceof Heartbeat) {
				ctx.writeAndFlush(msg);
			} else if (msg instanceof Agreement) {
				byte[] data = ((Agreement) msg).getData();
				final byte[] aesKey = AesCrypto.genAesSalt();
				final byte[] rsalt = ArrayUtil.subarray(data, 0, 8);
				PublicKeyCipher rsaCipher = new RsaCrypto.PublicKeyCipher(ArrayUtil.subarray(data, 8, data.length - 8));
				ctx.writeAndFlush(new Agreement(rsaCipher.encrypt(aesKey))).addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							AttributeHelper.setCipher(ctx.channel(), new AesCrypto(connact(rsalt, aesKey)));
							// ctx.channel().attr(AttributeHelper.CIPHER_KEY).set(new
							// AesCrypto(connact(rsalt, aesKey)));
						}
					}

					private byte[] connact(byte[] abytes, byte[] bbytes) {
						byte[] result = new byte[abytes.length + bbytes.length];
						System.arraycopy(abytes, 0, result, 0, abytes.length);
						System.arraycopy(bbytes, 0, result, abytes.length, bbytes.length);
						return result;
					}
				});
			}

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			LOG.error("untreated exception", cause);
			ctx.close();
		}
	}
}
