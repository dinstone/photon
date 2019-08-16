package com.dinstone.photon.transport.server;

import java.io.IOException;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.transport.TransportConfig;

public class AcceptorTest {
    private static final Logger LOG = LoggerFactory.getLogger(AcceptorTest.class);

    public static void main(String[] args) throws IOException {
        Acceptor acceptor = new Acceptor(new TransportConfig());
        acceptor.setMessageProcessor(new MessageProcessor() {

            @Override
            public void process(MessageContext context, Notice notice) {
                LOG.info("notice is {}", notice.getContent());
            }

            @Override
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
        });

        acceptor.bind();

        System.in.read();

        acceptor.destroy();
    }

}
