package com.dinstone.photon.handler;

import com.dinstone.photon.session.Session;

public interface MessageHandler {
    public void handle(final Session session, Object msg);
}
