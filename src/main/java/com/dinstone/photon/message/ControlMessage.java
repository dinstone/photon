package com.dinstone.photon.message;

public abstract class ControlMessage implements Message {

    private byte version = 0x1;

    private Type type;

    private int id;

    public ControlMessage(Type type) {
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

    public Type getType() {
        return type;
    }

    @Override
    public Headers getHeaders() {
        return null;
    }

    @Override
    public Object getContent() {
        return null;
    }
}
