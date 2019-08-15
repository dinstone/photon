package com.dinstone.photon.handler;

import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.session.ResponseFuture;
import com.dinstone.photon.session.Session;

public class ResponseHandler implements MessageHandler<Response> {

    @Override
    public void handle(MessageContext context, Response response) {
        Session session = AttributeHelper.getSession(context.getChannelContext().channel());
        ResponseFuture future = session.removeFuture(response.getId());
        if (future != null) {
            future.setResult(response);
        }

    }

}
