package com.dinstone.photon.message;

import com.dinstone.photon.serialization.SerializerType;

public class Response extends AbstractMessage {

    private Status status;

    public Response() {
        super(MessageType.RESPONSE);
    }

    public Response(SerializerType serializerType, int messageId, HeaderMap header, Object content, Status status) {
        super(MessageType.RESPONSE, serializerType, messageId, header, content);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
