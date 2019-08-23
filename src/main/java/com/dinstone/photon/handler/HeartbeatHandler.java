package com.dinstone.photon.handler;

import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.processor.MessageProcessor;

public class HeartbeatHandler implements MessageHandler<Heartbeat> {

    @Override
    public void handle(MessageContext context, MessageProcessor processor, Heartbeat heartbeat) {
        if (heartbeat.isPing()) {
            context.getChannelContext().writeAndFlush(heartbeat.pong());
        } else {
            // ignore
        }
    }

}