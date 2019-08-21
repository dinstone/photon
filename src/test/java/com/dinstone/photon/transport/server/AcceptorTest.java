package com.dinstone.photon.transport.server;

import java.net.InetSocketAddress;
import java.security.cert.X509Certificate;

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

import io.netty.handler.ssl.util.SelfSignedCertificate;

public class AcceptorTest {
    private static final Logger LOG = LoggerFactory.getLogger(AcceptorTest.class);

    public static void main(String[] args) throws Exception {
        AcceptOptions acceptOptions = new AcceptOptions();
        acceptOptions.setEnableSsl(true);
        SelfSignedCertificate cert = new SelfSignedCertificate();
        acceptOptions.setPrivateKey(cert.key());
        acceptOptions.setCertChain(new X509Certificate[] { cert.cert() });
        Acceptor acceptor = new Acceptor(acceptOptions);
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
