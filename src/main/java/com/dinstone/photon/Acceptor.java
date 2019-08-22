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
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
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

        bossGroup = new NioEventLoopGroup(options.getAcceptSize(), new DefaultThreadFactory("N4A-Boss"));
        workGroup = new NioEventLoopGroup(options.getWorkerSize(), new DefaultThreadFactory("N4A-Work"));

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

        applyConnectionOptions(bootstrap);

        int processorSize = options.getProcessorSize();
        if (processorSize > 0) {
            NamedThreadFactory threadFactory = new NamedThreadFactory("N4A-Processor");
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
        LOG.info("netty acceptance bind on {}", sa);

        return this;
    }

    private void applyConnectionOptions(ServerBootstrap bootstrap) {
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
        bootstrap.childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
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
            int currentConnectioncount = ConnectionManager.connectionCount();
            if (currentConnectioncount >= maxConnectionCount) {
                ctx.close();
                LOG.warn("connection count is too big: limit={},current={}", maxConnectionCount,
                        currentConnectioncount);
            } else {
                Connection session = new DefaultConnection(ctx.channel());
                ConnectionManager.addConnection(ctx.channel(), session);
                AttributeHelper.setConnection(ctx.channel(), session);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ConnectionManager.delConnection(ctx.channel());

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
