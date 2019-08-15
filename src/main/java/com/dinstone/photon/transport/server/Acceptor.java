package com.dinstone.photon.transport.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.handler.HeartbeatHandler;
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.handler.NoticeHandler;
import com.dinstone.photon.handler.RequestHandler;
import com.dinstone.photon.handler.ResponseHandler;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.session.DefaultSession;
import com.dinstone.photon.session.Session;
import com.dinstone.photon.session.SessionManager;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.util.concurrent.DefaultThreadFactory;

public class Acceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Acceptor.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    private ExecutorService executorService;

    private MessageProcessor messageProcessor;

    private Map<Class<?>, MessageHandler<?>> handlers = new ConcurrentHashMap<>();

    public Acceptor() {
        regist(Request.class, new RequestHandler());
        regist(Response.class, new ResponseHandler());
        regist(Notice.class, new NoticeHandler());
        regist(Heartbeat.class, new HeartbeatHandler());
    }

    private <T> void regist(Class<T> messageType, MessageHandler<T> messageHandler) {
        if (handlers.containsKey(messageType)) {
            throw new IllegalStateException("Already a handler registered with type " + messageType);
        }
        handlers.put(messageType, messageHandler);
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public Acceptor bind() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("N4A-Boss"));
        workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("N4A-Work"));

        ServerBootstrap boot = new ServerBootstrap().group(bossGroup, workGroup);
        boot.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
                ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

                ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(60, 0, 0));
                ch.pipeline().addLast("ServerHandler", new ServerHandler());
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

    private class ServerHandler extends ChannelInboundHandlerAdapter {

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
            int currentConnectioncount = SessionManager.sessionCount();
            if (currentConnectioncount >= maxConnectionCount) {
                ctx.close();
                LOG.warn("connection count is too big: limit={},current={}", maxConnectionCount,
                        currentConnectioncount);
            } else {
                Session session = new DefaultSession(ctx.channel());
                SessionManager.addSession(ctx.channel(), session);
                AttributeHelper.setSession(ctx.channel(), session);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            SessionManager.delSession(ctx.channel());

            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            LOG.info("server received message : {}", msg);

            MessageHandler<Object> messageHandler = (MessageHandler<Object>) handlers.get(msg.getClass());
            if (messageHandler != null) {
                messageHandler.handle(new MessageContext(ctx, messageProcessor), msg);
            }

//            if (msg instanceof Agreement) {
//                byte[] data = ((Agreement) msg).getData();
//                final byte[] aesKey = ArrayUtil.concat(ArrayUtil.copy(data, 0, 8), AesCrypto.genAesSalt());
//                PublicKeyCipher rsaCipher = new RsaCrypto.PublicKeyCipher(ArrayUtil.copy(data, 8, data.length - 8));
//                ctx.writeAndFlush(new Agreement(rsaCipher.encrypt(aesKey))).addListener(new ChannelFutureListener() {
//
//                    @Override
//                    public void operationComplete(ChannelFuture future) throws Exception {
//                        if (future.isSuccess()) {
//                            AttributeHelper.setCipher(ctx.channel(), new AesCrypto(aesKey));
//                        }
//                    }
//
//                });
//            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("untreated exception", cause);
            ctx.close();
        }
    }
}
