package com.dinstone.photon.handler;

import com.dinstone.photon.message.Request;
import com.dinstone.photon.processor.MessageProcessor;

public class RequestHandler implements MessageHandler<Request> {

    @Override
    public void handle(MessageContext context, MessageProcessor processor, Request msg) {
        processor.process(context, msg);
    }

}
