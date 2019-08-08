package com.dinstone.photon.crypto;

public interface Crypto {

	public byte[] encrypt(byte[] bytes) throws Exception;

	public byte[] decrypt(byte[] bytes) throws Exception;
}
