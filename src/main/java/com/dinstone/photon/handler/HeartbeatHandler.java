package com.dinstone.photon.handler;

import com.dinstone.photon.message.Heartbeat;

public class HeartbeatHandler implements MessageHandler<Heartbeat> {

    @Override
    public void handle(MessageContext context, Heartbeat heartbeat) {
        if (heartbeat.isPing()) {
            context.getChannelContext().writeAndFlush(heartbeat.pong());
        } else {
            // ignore
        }
    }
}