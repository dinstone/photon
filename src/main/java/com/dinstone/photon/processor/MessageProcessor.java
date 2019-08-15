package com.dinstone.photon.processor;

import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;

public interface MessageProcessor {

    void process(MessageContext context, Request request);

    void process(MessageContext context, Notice notice);

}
