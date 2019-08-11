package com.dinstone.photon.message;

public class Request extends AbstractMessage {

    private int timeout;

    public Request() {
        super(MessageType.REQUEST);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
