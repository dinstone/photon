package com.dinstone.photon.codec;

import com.dinstone.photon.protocol.Agreement;

import io.netty.buffer.ByteBuf;

public class AgreementCodec implements MessageCodec<Agreement> {

	public byte getCodecId() {
		return 1;
	}

    @Override
    public Agreement decode(ByteBuf in) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf encode(Agreement message) {
        // TODO Auto-generated method stub
        return null;
    }

}
