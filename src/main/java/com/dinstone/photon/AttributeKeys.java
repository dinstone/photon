package com.dinstone.photon;

import com.dinstone.photon.crypto.Cipher;
import com.dinstone.photon.protocol.Agreement;
import com.dinstone.photon.session.Session;

import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;

public class AttributeKeys {

	public static final AttributeKey<Cipher> CIPHER_KEY = AttributeKey.valueOf("cipher.key");

	public static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("session.key");

	public static final AttributeKey<Promise<Agreement>> PROMISE_KEY = AttributeKey.valueOf("promise.key");

}
