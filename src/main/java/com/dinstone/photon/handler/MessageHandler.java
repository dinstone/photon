package com.dinstone.photon.handler;

import com.dinstone.photon.processor.MessageProcessor;

public interface MessageHandler<T> {

    public void handle(final MessageContext context, MessageProcessor processor, T msg);
}
