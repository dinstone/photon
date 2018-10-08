package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Cipher;

import io.netty.channel.ChannelFuture;

public interface Session {

	Cipher getCipher();

	public void setCipher(Cipher cipher);

	ChannelFuture write(Object msg);

	boolean isActive();
}
