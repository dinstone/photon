package com.dinstone.photon.message;

import com.dinstone.photon.serialization.SerializerType;

public class Heartbeat extends AbstractMessage {

    public Heartbeat(int messageId) {
        super(MessageType.HEARTBEAT, SerializerType.JACKSON, messageId, null, null);
    }

    public Heartbeat(SerializerType serializerType, int messageId, HeaderMap header) {
        super(MessageType.HEARTBEAT, serializerType, messageId, header, null);
    }

}
