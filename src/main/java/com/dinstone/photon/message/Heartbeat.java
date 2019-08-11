package com.dinstone.photon.message;

public class Heartbeat extends AbstractMessage {

    public Heartbeat(int messageId) {
        super(MessageType.HEARTBEAT);
        setMessageId(messageId);
    }

}
