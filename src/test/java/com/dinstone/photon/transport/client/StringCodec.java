package com.dinstone.photon.transport.client;

import com.dinstone.photon.codec.MessageCodec;

public class StringCodec implements MessageCodec<String> {

	@Override
	public byte getCodecId() {
		return -1;
	}

	@Override
	public String decode(byte[] datas) {
		return new String(datas);
	}

	@Override
	public byte[] encode(String message) {
		return message.getBytes();
	}

}
