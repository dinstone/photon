package com.dinstone.photon.session;

import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.crypto.Crypto;
import com.dinstone.photon.transport.NetworkInterfaceUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class DefaultSession implements Session {

	private String code;

	private Channel channel;

	public DefaultSession(Channel channel) {
		this.channel = channel;
		this.code = NetworkInterfaceUtil.addressLabel(channel.remoteAddress(), channel.localAddress());
	}

	public Crypto getCrypto() {
		return AttributeHelper.getCipher(channel);
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
