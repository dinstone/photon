package com.dinstone.photon.message;

import com.dinstone.photon.serialization.SerializerType;

public class Notice extends AbstractMessage {

    private String address;

    public Notice() {
        super(MessageType.NOTICE);
    }

    public Notice(SerializerType serializerType, int messageId, HeaderMap header, Object content, String address) {
        super(MessageType.NOTICE, serializerType, messageId, header, content);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
