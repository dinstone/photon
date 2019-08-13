package com.dinstone.photon.handler;

import com.dinstone.photon.message.Response;
import com.dinstone.photon.session.ResponseFuture;
import com.dinstone.photon.session.Session;

public class ResponseHandler implements MessageHandler {

    @Override
    public void handle(Session session, Object msg) {
        if (msg instanceof Response) {
            Response response = (Response) msg;
            ResponseFuture future = session.removeFuture(response.getId());
            if (future != null) {
                future.setResult(response);
            }
        }
    }

}
