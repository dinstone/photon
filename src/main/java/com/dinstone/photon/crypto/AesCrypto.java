package com.dinstone.photon.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AesCrypto {

	public static final String KEY_ALGORITHM = "AES";

	private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";

	public static byte[] genAesKey() {
		byte[] result = new byte[16];
		Random random = new Random();
		writeLong(random.nextLong(), result, 0);
		writeLong(random.nextLong(), result, 8);
		return result;
	}

	private static void writeLong(long value, byte[] out, int destPos) {
		byte[] buffer = new byte[8];
		buffer[0] = (byte) (value >>> 56);
		buffer[1] = (byte) (value >>> 48);
		buffer[2] = (byte) (value >>> 40);
		buffer[3] = (byte) (value >>> 32);
		buffer[4] = (byte) (value >>> 24);
		buffer[5] = (byte) (value >>> 16);
		buffer[6] = (byte) (value >>> 8);
		buffer[7] = (byte) (value);
		System.arraycopy(buffer, 0, out, destPos, 8);
	}

	private static Cipher createCipher(Key key, int opmodule)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(KEY_TRANSFORMATION);
		cipher.init(opmodule, key);
		return cipher;
	}

	public static class KeyCipher implements com.dinstone.photon.crypto.Cipher{

		private Cipher encriptor;

		private Cipher decriptor;

		public KeyCipher(byte[] encodedKey) {
			try {
				SecretKeySpec key = new SecretKeySpec(encodedKey, KEY_ALGORITHM);
				this.encriptor = createCipher(key, Cipher.ENCRYPT_MODE);
				this.decriptor = createCipher(key, Cipher.DECRYPT_MODE);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public byte[] encrypt(byte[] bytes) throws Exception {
			return encriptor.doFinal(bytes);
		}

		public byte[] decrypt(byte[] bytes) throws Exception {
			return decriptor.doFinal(bytes);
		}

	}

}
