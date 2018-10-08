package com.dinstone.photon.codec;

import com.dinstone.photon.protocol.Crypt;

public class CryptCodec implements MessageCodec<Crypt> {

	@Override
	public byte getCodecId() {
		return 1;
	}

	@Override
	public Crypt decode(byte[] datas) {
		return new Crypt(datas);
	}

	@Override
	public byte[] encode(Crypt message) {
		return message.getData();
	}

}
