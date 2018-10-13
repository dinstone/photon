package com.dinstone.photon;

public class ArrayUtil {

	public static byte[] concat(byte[] ibytes, byte[] jbytes) {
		byte[] result = new byte[ibytes.length + jbytes.length];
		System.arraycopy(ibytes, 0, result, 0, ibytes.length);
		System.arraycopy(jbytes, 0, result, ibytes.length, jbytes.length);
		return result;
	}

	public static byte[] subarray(byte[] b, int off, int length) {
		byte[] b1 = new byte[length];
		System.arraycopy(b, off, b1, 0, length);
		return b1;
	}
}
