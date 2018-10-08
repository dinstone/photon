package com.dinstone.photon.protocol;

public class Crypt {

	private byte[] data;

	public Crypt(byte[] encoded) {
		this.data = encoded;
	}

	public byte[] getData() {
		return data;
	}

}
