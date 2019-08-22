package com.dinstone.photon.connection;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.channel.ChannelFuture;

public interface Connection {

    String sessionId();

    boolean isActive();

    void notify(Notice notice);

    ChannelFuture write(Message message);

    Response sync(Request request) throws Exception;
    
    ResponseFuture async(Request request) throws Exception;

}