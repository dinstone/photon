package com.dinstone.photon.codec;

import com.dinstone.photon.protocol.Agreement;

public class AgreementCodec implements MessageCodec<Agreement> {

	@Override
	public byte getCodecId() {
		return 1;
	}

	@Override
	public Agreement decode(byte[] datas) {
		return new Agreement(datas);
	}

	@Override
	public byte[] encode(Agreement message) {
		return message.getData();
	}

}
