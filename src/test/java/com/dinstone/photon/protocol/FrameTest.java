package com.dinstone.photon.protocol;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.dinstone.photon.protocol.Frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class FrameTest {

	@Test
	public void test00() throws Exception {
		byte[] message = new byte[1024];
		for (int i = 0; i < message.length; i++) {
			message[i] = 'a';
		}

		ByteBuf out = ByteBufAllocator.DEFAULT.buffer(1200);
		new Frame((byte) 1, message).enzip().encode(out);

		byte[] actuals = new Frame().decode(out).dezip().getDatas();
		assertArrayEquals(message, actuals);

	}

}
