package com.dinstone.photon.message;

import com.dinstone.photon.serialization.SerializerType;

public abstract class AbstractMessage implements Message {

    private byte messageVersion = 0x1;

    private MessageType messageType;

    private SerializerType serializerType;

    private int messageId;

    private HeaderMap headers;

    private Object content;

    public AbstractMessage(MessageType messageType) {
        this.messageType = messageType;
    }

    public AbstractMessage(MessageType messageType, SerializerType serializerType, int messageId, HeaderMap headers,
            Object content) {
        this.serializerType = serializerType;
        this.messageType = messageType;
        this.messageId = messageId;
        this.headers = headers;
        this.content = content;
    }

    @Override
    public byte getMessageVersion() {
        return messageVersion;
    }

    @Override
    public int getMessageId() {
        return messageId;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public SerializerType getSerializerType() {
        return serializerType;
    }

    @Override
    public HeaderMap getHeaders() {
        return headers;
    }

    @Override
    public Object getContent() {
        return content;
    }

    public void setMessageVersion(byte messageVersion) {
        this.messageVersion = messageVersion;
    }

    public void setSerializerType(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setHeaders(HeaderMap headers) {
        this.headers = headers;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}
