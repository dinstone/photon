package com.dinstone.photon.handler;

import java.util.Map;
import java.util.concurrent.Executor;

import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.connection.ResponseFuture;

import io.netty.channel.ChannelHandlerContext;

public class MessageContext {

    private ChannelHandlerContext channelContext;

    private Executor defaultExecutor;

    public MessageContext(ChannelHandlerContext channelContext, Executor defaultExecutor) {
        this.channelContext = channelContext;
        this.defaultExecutor = defaultExecutor;
    }

    public ChannelHandlerContext getChannelContext() {
        return channelContext;
    }

    public Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    public Connection getConnection() {
        return AttributeHelper.getConnection(channelContext.channel());
    }

    public Map<Integer, ResponseFuture> getResponseFutures() {
        return AttributeHelper.futureMap(channelContext.channel());
    }

}
