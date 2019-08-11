package com.dinstone.photon.transport.server;

import java.io.IOException;

import com.dinstone.photon.handler.MessageHandler;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.session.Session;

public class AcceptorTest {

    public static void main(String[] args) throws IOException {
        Acceptor acceptor = new Acceptor();
        acceptor.regist(Request.class, new MessageHandler() {

            @Override
            public void handle(Session session, Object msg) {
                if (msg instanceof Request) {
                    Request request = (Request) msg;
                    Response response = new Response();
                    response.setMessageId(request.getMessageId());
                    response.setSerializerType(request.getSerializerType());
                    response.setStatus(Status.SUCCESS);
                    response.setContent(new byte[] { 32 });
                    session.write(response);
                }
            }
        });

        acceptor.bind();

        System.in.read();

        acceptor.destroy();
    }

}
