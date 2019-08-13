package com.dinstone.photon.message;

public class Notice extends ExchangeMessage {

    private String address;

    public Notice() {
        super(Type.NOTICE);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
