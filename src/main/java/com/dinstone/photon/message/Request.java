package com.dinstone.photon.message;

import com.dinstone.photon.serialization.SerializerType;

public class Request extends AbstractMessage {

    private int timeout;

    public Request() {
        super(MessageType.REQUEST);
    }

    public Request(SerializerType serializerType, int messageId, HeaderMap header, Object content, int timeout) {
        super(MessageType.REQUEST, serializerType, messageId, header, content);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
