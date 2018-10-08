package com.dinstone.photon.crypto;

public interface Cipher {

	public byte[] encrypt(byte[] bytes) throws Exception;

	public byte[] decrypt(byte[] bytes) throws Exception;
}
