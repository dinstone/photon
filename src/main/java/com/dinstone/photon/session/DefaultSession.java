package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Cipher;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class DefaultSession implements Session {

	private Channel channel;

	private Cipher cipher;

	public DefaultSession() {
	}

	public DefaultSession(Channel channel) {
		this.channel = channel;
	}

	public Cipher getCipher() {
		return cipher;
	}

	public void setCipher(Cipher cipher) {
		this.cipher = cipher;
	}

	@Override
	public ChannelFuture write(Object msg) {
		return channel.writeAndFlush(msg);
	}

	@Override
	public boolean isActive() {
		return channel.isActive();
	}


}
