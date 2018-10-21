package com.dinstone.photon.protocol;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.dinstone.photon.crypto.AesCrypto;
import com.dinstone.photon.crypto.Cipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class FrameTest {

	@Test
	public void test00() throws Exception {
		byte[] message = new byte[512000];
		for (int i = 0; i < message.length; i++) {
			message[i] = 'a';
		}

		ByteBuf out = ByteBufAllocator.DEFAULT.buffer(1200);
		new Frame((byte) 1, message).enzip().encode(out);

		byte[] actuals = new Frame().decode(out).dezip().getDatas();
		assertArrayEquals(message, actuals);

	}

	@Test
	public void test01() throws Exception {
		long s = System.currentTimeMillis();

		byte[] message = new byte[512000];
		for (int i = 0; i < message.length; i++) {
			message[i] = 'a';
		}
		for (int j = 0; j < 10000; j++) {
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer(1200);
			new Frame((byte) 1, message).enzip().encode(out);

			byte[] actuals = new Frame().decode(out).dezip().getDatas();

			out.release();
		}

		long e = System.currentTimeMillis();
		System.out.println("zip take's " + (e - s) + "ms, qps=" + 10000 * 1000 / (e - s));

	}

	@Test
	public void test02() throws Exception {
		long s = System.currentTimeMillis();
	
		byte[] message = new byte[512000];
		for (int i = 0; i < message.length; i++) {
			message[i] = 'a';
		}
		for (int j = 0; j < 10000; j++) {
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer(1200);
			new Frame((byte) 1, message).encode(out);
	
			byte[] actuals = new Frame().decode(out).getDatas();
	
			out.release();
		}
	
		long e = System.currentTimeMillis();
		System.out.println("non take's " + (e - s) + "ms, qps=" + 10000 * 1000 / (e - s));
	
	}

	@Test
	public void test03() throws Exception {
		long s = System.currentTimeMillis();

		byte[] message = new byte[512000];
		for (int i = 0; i < message.length; i++) {
			message[i] = 'a';
		}

		Cipher cipher = new AesCrypto(AesCrypto.genAesKey());
		for (int j = 0; j < 10000; j++) {
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer(1200);
			new Frame((byte) 1, message).encrypt(cipher).encode(out);

			byte[] actuals = new Frame().decode(out).decrypt(cipher).getDatas();

			out.release();
		}

		long e = System.currentTimeMillis();
		System.out.println("cipher take's " + (e - s) + "ms, qps=" + 10000 * 1000 / (e - s));

	}
	
	@Test
	public void test04() throws Exception {
		long s = System.currentTimeMillis();

		byte[] message = new byte[512000];
		for (int i = 0; i < message.length; i++) {
			message[i] = 'a';
		}

		Cipher cipher = new AesCrypto(AesCrypto.genAesKey());
		for (int j = 0; j < 10000; j++) {
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer(1200);
			new Frame((byte) 1, message).encrypt(cipher).enzip().encode(out);

			byte[] actuals = new Frame().decode(out).dezip().decrypt(cipher).getDatas();

			out.release();
		}

		long e = System.currentTimeMillis();
		System.out.println("cipher & zip take's " + (e - s) + "ms, qps=" + 10000 * 1000 / (e - s));

	}

}
