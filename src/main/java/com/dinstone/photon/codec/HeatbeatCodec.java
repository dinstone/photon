package com.dinstone.photon.codec;

import java.nio.ByteBuffer;

import com.dinstone.photon.protocol.Heartbeat;

public class HeatbeatCodec implements MessageCodec<Heartbeat> {

	@Override
	public byte getCodecId() {
		return 0;
	}

	@Override
	public Heartbeat decode(byte[] datas) {
		return new Heartbeat(ByteBuffer.wrap(datas).getInt());
	}

	@Override
	public byte[] encode(Heartbeat message) {
		return ByteBuffer.allocate(4).putInt(message.getTick()).array();
	}

}
