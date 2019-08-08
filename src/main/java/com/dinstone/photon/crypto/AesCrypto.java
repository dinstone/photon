package com.dinstone.photon.crypto;

import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesCrypto implements Crypto {

    public static final String KEY_ALGORITHM = "AES";

    private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private final SecretKeySpec key;

    public AesCrypto(byte[] encodedKey) {
        try {
            key = new SecretKeySpec(encodedKey, KEY_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] genAesKey() {
        byte[] result = new byte[16];
        Random random = new Random();
        System.arraycopy(genLong(random), 0, result, 0, 8);
        System.arraycopy(genLong(random), 0, result, 8, 8);
        return result;
    }

    public static byte[] genAesSalt() {
        Random random = new Random();
        return genLong(random);
    }

    private static byte[] genLong(Random random) {
        byte[] buffer = new byte[8];
        long value = random.nextLong();
        buffer[0] = (byte) (value >>> 56);
        buffer[1] = (byte) (value >>> 48);
        buffer[2] = (byte) (value >>> 40);
        buffer[3] = (byte) (value >>> 32);
        buffer[4] = (byte) (value >>> 24);
        buffer[5] = (byte) (value >>> 16);
        buffer[6] = (byte) (value >>> 8);
        buffer[7] = (byte) (value);
        return buffer;
    }

    private static Cipher createCipher(Key key, int opmodule) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_TRANSFORMATION);
        cipher.init(opmodule, key);
        return cipher;
    }

    public byte[] encrypt(byte[] bytes) throws Exception {
        return createCipher(key, Cipher.ENCRYPT_MODE).doFinal(bytes);
    }

    public byte[] decrypt(byte[] bytes) throws Exception {
        return createCipher(key, Cipher.DECRYPT_MODE).doFinal(bytes);
    }

}
