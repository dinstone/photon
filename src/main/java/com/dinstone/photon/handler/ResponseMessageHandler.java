package com.dinstone.photon.handler;

import com.dinstone.photon.message.Response;
import com.dinstone.photon.session.Session;

public class ResponseMessageHandler implements MessageHandler {

    @Override
    public void handle(Session session, Object msg) {
        if(msg instanceof Response) {
//            f = session.remove((Response )msg.);
        }
    }

}
