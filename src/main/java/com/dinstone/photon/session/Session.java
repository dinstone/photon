package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Crypto;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.channel.ChannelFuture;

public interface Session {

    boolean isActive();

    Crypto getCrypto();

    ChannelFuture write(Message msg);

    void oneway(Notice notice);

    Response sync(Request request) throws Exception;

    ResponseFuture remove(int messageId);
}
