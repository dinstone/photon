package com.dinstone.photon.message;

public class Heartbeat extends ControlMessage {

    /**
     * true:ping / false:pong
     */
    private boolean tick;

    public Heartbeat(int messageId, boolean tick) {
        super(Type.HEARTBEAT);
        setId(messageId);
        this.tick = tick;
    }

    public Heartbeat ping() {
        this.tick = true;
        return this;
    }

    public Heartbeat pong() {
        this.tick = false;
        return this;
    }

    public boolean getTick() {
        return tick;
    }

    public boolean isPing() {
        return tick;
    }

    public boolean isPong() {
        return !tick;
    }

    @Override
    public String toString() {
        return "Heartbeat[id=" + getId() + ",tick=" + tick + "]";
    }

}
