package com.dinstone.photon.session;

import com.dinstone.photon.crypto.Crypto;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.channel.ChannelFuture;

public interface Session {
    
    String sessionId();

    boolean isActive();

    Crypto getCrypto();

    void oneway(Notice notice);

    ChannelFuture write(Message msg);

    Response sync(Request request) throws Exception;

    ResponseFuture removeFuture(int messageId);

    void addFuture(ResponseFuture future);
}
