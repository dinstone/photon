package com.dinstone.photon;

import com.dinstone.photon.crypto.Cipher;
import com.dinstone.photon.protocol.Agreement;
import com.dinstone.photon.session.Session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;

public class AttributeHelper {

	private static final AttributeKey<Cipher> CIPHER_KEY = AttributeKey.valueOf("cipher.key");

	private static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("session.key");

	private static final AttributeKey<Promise<Agreement>> PROMISE_KEY = AttributeKey.valueOf("promise.key");

	public static void setPromise(Channel channel, Promise<Agreement> promise) {
		channel.attr(AttributeHelper.PROMISE_KEY).set(promise);
	}

	public static Promise<Agreement> getPromise(Channel channel) {
		return channel.attr(AttributeHelper.PROMISE_KEY).get();
	}

	public static void setCipher(Channel channel, Cipher cipher) {
		channel.attr(AttributeHelper.CIPHER_KEY).set(cipher);
	}

	public static Cipher getCipher(Channel channel) {
		return channel.attr(AttributeHelper.CIPHER_KEY).get();
	}

	public static void setSession(Channel channel, Session session) {
		channel.attr(AttributeHelper.SESSION_KEY).set(session);
	}

}
