package com.dinstone.photon.message;

public abstract class ExchangeMessage implements Message {

    private byte version = 0x1;

    private Type type;

    private int id;

    private Headers headers;

    private byte[] content;

    public ExchangeMessage(Type type) {
        this.type = type;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Type getType() {
        return type;
    }

}
