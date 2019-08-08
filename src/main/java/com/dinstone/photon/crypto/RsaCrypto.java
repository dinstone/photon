package com.dinstone.photon.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class RsaCrypto {

	private static final String KEY_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

	public static final String KEY_ALGORITHM = "RSA";

	private static final int KEY_SIZE = 1024;

	public static KeyPair generateKeyPair() throws Exception {
		return generateKeyPair(KEY_SIZE);
	}

	public static KeyPair generateKeyPair(int keySize) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(keySize);
		return keyPairGenerator.generateKeyPair();
	}

	private static Cipher createCipher(Key key, int opmodule)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(KEY_TRANSFORMATION);
		cipher.init(opmodule, key);
		return cipher;
	}

	public static class PublicKeyCipher implements com.dinstone.photon.crypto.Crypto {

		private PublicKey publicKey;

		public PublicKeyCipher(byte[] encodedKey) {
			try {
				X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedKey);
				KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
				publicKey = keyFactory.generatePublic(x509KeySpec);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public byte[] encrypt(byte[] bytes) throws Exception {
			return createCipher(publicKey, Cipher.ENCRYPT_MODE).doFinal(bytes);
		}

		public byte[] decrypt(byte[] bytes) throws Exception {
			return createCipher(publicKey, Cipher.DECRYPT_MODE).doFinal(bytes);
		}

	}

	public static class PrivateKeyCipher implements com.dinstone.photon.crypto.Crypto {

		private PrivateKey privateKey;

		public PrivateKeyCipher(byte[] encodedKey) {
			try {
				PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encodedKey);
				KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
				privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public byte[] encrypt(byte[] bytes) throws Exception {
			return createCipher(privateKey, Cipher.ENCRYPT_MODE).doFinal(bytes);
		}

		public byte[] decrypt(byte[] bytes) throws Exception {
			return createCipher(privateKey, Cipher.DECRYPT_MODE).doFinal(bytes);
		}

	}

}