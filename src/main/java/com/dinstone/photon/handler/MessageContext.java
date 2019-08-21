package com.dinstone.photon.handler;

import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.connection.ResponseFuture;
import com.dinstone.photon.processor.MessageProcessor;

import io.netty.channel.ChannelHandlerContext;

public class MessageContext {

    private ChannelHandlerContext channelContext;

    private MessageProcessor messageProcessor;

    public MessageContext(ChannelHandlerContext channelContext, MessageProcessor messageProcessor) {
        this.channelContext = channelContext;
        this.messageProcessor = messageProcessor;
    }

    public ChannelHandlerContext getChannelContext() {
        return channelContext;
    }

    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public ResponseFuture removeResponseFuture(int id) {
        return AttributeHelper.futureMap(channelContext.channel()).remove(id);
    }

    public Connection getConnection() {
        return AttributeHelper.getConnection(channelContext.channel());
    }

}
