package com.dinstone.photon.codec;

public interface MessageCodec<M> {

	byte getCodecId();

	M decode(byte[] datas);

	byte[] encode(M message);

}
