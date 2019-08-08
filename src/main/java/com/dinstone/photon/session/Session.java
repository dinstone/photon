package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Crypto;

import io.netty.channel.ChannelFuture;

public interface Session {

	boolean isActive();

	Crypto getCipher();

	ChannelFuture write(Object msg);
}
