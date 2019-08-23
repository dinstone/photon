package com.dinstone.photon.handler;

import com.dinstone.photon.connection.ResponseFuture;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.processor.MessageProcessor;

public class ResponseHandler implements MessageHandler<Response> {

    @Override
    public void handle(MessageContext context, MessageProcessor processor, Response response) {
        ResponseFuture future = context.removeResponseFuture(response.getId());
        if (future != null) {
            future.setResult(response);
        }

    }

}
