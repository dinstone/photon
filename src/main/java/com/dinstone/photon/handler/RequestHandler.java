package com.dinstone.photon.handler;

import com.dinstone.photon.message.Request;

public class RequestHandler implements MessageHandler<Request> {

    @Override
    public void handle(MessageContext context, Request msg) {
        context.getMessageProcessor().process(context, (Request) msg);
    }

}
