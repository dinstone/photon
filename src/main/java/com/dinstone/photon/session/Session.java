package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Cipher;

import io.netty.channel.ChannelFuture;

public interface Session {

	boolean isActive();

	Cipher getCipher();

	ChannelFuture write(Object msg);
}
