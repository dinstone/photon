package com.dinstone.photon.handler;

import com.dinstone.photon.connection.ResponseFuture;
import com.dinstone.photon.message.Response;

public class ResponseHandler implements MessageHandler<Response> {

    @Override
    public void handle(MessageContext context, Response response) {
        ResponseFuture future = context.removeResponseFuture(response.getId());
        if (future != null) {
            future.setResult(response);
        }

    }

}
