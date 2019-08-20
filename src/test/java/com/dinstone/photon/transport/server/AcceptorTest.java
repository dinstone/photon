package com.dinstone.photon.transport.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AcceptOptions;
import com.dinstone.photon.Acceptor;
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.processor.MessageProcessor;

public class AcceptorTest {
    private static final Logger LOG = LoggerFactory.getLogger(AcceptorTest.class);

    public static void main(String[] args) throws IOException {
        Acceptor acceptor = new Acceptor(new AcceptOptions());
        acceptor.setMessageProcessor(new MessageProcessor() {

            public void process(MessageContext context, Notice notice) {
                LOG.info("notice is {}", notice.getContent());
            }

            public void process(MessageContext context, Request request) {
                LOG.info("response is {}", request.getContent());
                Notice notice = new Notice();
                notice.setAddress("");
                notice.setContent(request.getContent());
                context.getChannelContext().writeAndFlush(notice);

                Response response = new Response();
                response.setId(request.getId());
                response.setStatus(Status.SUCCESS);
                response.setContent(request.getContent());
                context.getChannelContext().writeAndFlush(response);
            }

            @Override
            public void process(MessageContext context, Message message) {
                if (message instanceof Request) {
                    process(context, (Request) message);
                }

                if (message instanceof Notice) {
                    process(context, (Notice) message);
                }
            }
        });

        acceptor.bind(new InetSocketAddress("127.0.0.1", 4444));

        System.in.read();

        acceptor.destroy();
    }

}
