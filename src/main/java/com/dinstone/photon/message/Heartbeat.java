package com.dinstone.photon.message;

public class Heartbeat extends ControlMessage {

    public Heartbeat(int messageId) {
        super(Type.HEARTBEAT);
        setId(messageId);
    }

}
