package com.dinstone.photon.handler;

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

    public void setChannelContext(ChannelHandlerContext channelContext) {
        this.channelContext = channelContext;
    }

    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

}
