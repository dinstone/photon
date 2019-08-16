package com.dinstone.photon;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.handler.HandlerManager;
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.session.DefaultSession;
import com.dinstone.photon.session.Session;
import com.dinstone.photon.session.SessionManager;
import com.dinstone.photon.transport.TransportConfig;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
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

    private TransportConfig transportConfig;

    public Acceptor(TransportConfig transportConfig) {
        this.transportConfig = transportConfig;
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        if (messageProcessor == null) {
            throw new IllegalArgumentException("messageProcessor is null");
        }
        this.messageProcessor = messageProcessor;
    }

    public Acceptor bind(SocketAddress sa) {
        checkMessageProcessor();

        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("N4A-Boss"));
        workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("N4A-Work"));

        ServerBootstrap boot = new ServerBootstrap().group(bossGroup, workGroup);
        boot.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (transportConfig.enableCrypt()) {
                    SSLEngine engine = createSslEngine(ch.alloc());
                    ch.pipeline().addFirst(new SslHandler(engine));
                }
                ch.pipeline().addLast("TransportDecoder", new TransportDecoder());
                ch.pipeline().addLast("TransportEncoder", new TransportEncoder());

                ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(60, 0, 0));
                ch.pipeline().addLast("ServerHandler", new ServerHandler());
            }
        });
        boot.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_BACKLOG, 128);
        boot.childOption(ChannelOption.SO_RCVBUF, 4 * 1024).childOption(ChannelOption.SO_SNDBUF, 4 * 1024)
                .childOption(ChannelOption.TCP_NODELAY, true);

        try {
            boot.bind(sa).sync();

            // int processorCount = transportConfig.getBusinessProcessorCount();
            // if (processorCount > 0) {
            // NamedThreadFactory threadFactory = new
            // NamedThreadFactory("N4A-BusinessProcessor");
            // executorService = Executors.newFixedThreadPool(processorCount,
            // threadFactory);
            // }
        } catch (Exception e) {
            throw new RuntimeException("can't bind service on " + sa, e);
        }
        LOG.info("netty acceptance bind on {}", sa);

        return this;
    }

    private void checkMessageProcessor() {
        if (messageProcessor == null) {
            throw new IllegalStateException("messageProcessor not set");
        }
    }

    protected SSLEngine createSslEngine(ByteBufAllocator byteBufAllocator) throws Exception {
        SelfSignedCertificate certificate = new SelfSignedCertificate();
        SslContextBuilder builder = SslContextBuilder.forServer(certificate.key(), certificate.cert());
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

            MessageHandler<Object> messageHandler = HandlerManager.find(msg.getClass());
            if (messageHandler != null) {
                messageHandler.handle(new MessageContext(ctx, messageProcessor), msg);
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("untreated exception", cause);
            ctx.close();
        }
    }
}
