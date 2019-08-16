package com.dinstone.photon.processor;

import com.dinstone.photon.handler.MessageContext;
import com.dinstone.photon.message.Message;

public interface MessageProcessor {

    void process(MessageContext context, Message message);

}
