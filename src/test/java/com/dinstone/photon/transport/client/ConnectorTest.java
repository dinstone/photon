package com.dinstone.photon.transport.client;

import java.net.InetSocketAddress;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.serialization.SerializerType;
import com.dinstone.photon.session.Session;
import com.dinstone.photon.transport.TransportConfig;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ConnectorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectorTest.class);

    public static void main(String[] args) throws Throwable {
        Connector connector = new Connector(new TransportConfig());
        connector.regist(Response.class, new MessageHandler() {

            @Override
            public void handle(Session session, Object msg) {
                if (msg instanceof Response) {
                    Response response = (Response) msg;
                    LOG.info("response stauts is {}", response.getStatus());
                }
            }
        });

        Session session = connector.createSession(new InetSocketAddress("127.0.0.1", 4444));
        LOG.info("channel active is {}", session.isActive());

        Request request = new Request();
        request.setMessageId(1);
        request.setSerializerType(SerializerType.JACKSON);
        request.setTimeout(10000);
        request.setContent("Hello World".getBytes());
        ChannelFuture cf = session.write(request);
        cf.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isDone()) {
                    future.channel();
                }
            }
        });

        System.in.read();

        connector.dispose();
    }

}
