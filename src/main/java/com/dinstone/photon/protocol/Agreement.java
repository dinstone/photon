package com.dinstone.photon.protocol;

public class Agreement {

	private byte[] data;

	public Agreement(byte[] encoded) {
		this.data = encoded;
	}

	public byte[] getData() {
		return data;
	}

}
