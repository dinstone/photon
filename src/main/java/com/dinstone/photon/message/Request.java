package com.dinstone.photon.message;

public class Request extends BurdenMessage {

    private int timeout;

    public Request() {
        super(Type.REQUEST);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
