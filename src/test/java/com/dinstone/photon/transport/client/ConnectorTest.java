package com.dinstone.photon.transport.client;

import java.net.InetSocketAddress;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.ConnectOptions;
import com.dinstone.photon.Connector;
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.processor.MessageProcessor;

public class ConnectorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectorTest.class);

    public static void main(String[] args) throws Throwable {
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setEnableSsl(true);
        Connector connector = new Connector(connectOptions);
        connector.setMessageProcessor(new MessageProcessor() {

            @Override
            public void process(MessageContext context, Message message) {
                if (message instanceof Notice) {
                    LOG.info("notice is {}", message.getContent());
                }
                if (message instanceof Request) {
                    LOG.info("response is {}", message.getContent());
                }
            }
        });

        Connection session = connector.connect(new InetSocketAddress("127.0.0.1", 4444));
        LOG.info("channel active is {}", session.isActive());

        Request request = new Request();
        request.setId(1);
        request.setTimeout(10000);
        request.setContent("Hello World".getBytes());

        Response response = session.sync(request);
        LOG.info("response is {}", response.getContent());

        System.in.read();

        connector.dispose();
    }

}
