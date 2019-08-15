package com.dinstone.photon.handler;

public interface MessageHandler<T> {

    public void handle(final MessageContext context, T msg);
}
