package com.dinstone.photon.message;

public class Response extends ExchangeMessage {

    private Status status;

    public Response() {
        super(Type.RESPONSE);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
