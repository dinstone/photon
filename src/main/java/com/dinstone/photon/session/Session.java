package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Crypto;
import com.dinstone.photon.message.Message;

import io.netty.channel.ChannelFuture;

public interface Session {

    boolean isActive();

    Crypto getCrypto();

    ChannelFuture write(Message msg);
}
