package com.dinstone.photon.transport;

import java.util.List;

import com.dinstone.photon.codec.CodecManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class MessageEncoder extends MessageToMessageEncoder<Object> {

    private final CodecManager codecManager;

    public MessageEncoder(CodecManager codecManager) {
        this.codecManager = codecManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        // MessageCodec<Object> messageCodec = (MessageCodec<Object>)
        // codecManager.find(msg);
        // Frame frame = new Frame(messageCodec.getCodecId(), messageCodec.encode(msg));
        // Crypto cipher = AttributeHelper.getCipher(ctx.channel());
        // if (cipher != null) {
        // frame.encrypt(cipher);
        // }
        //
        // out.add(frame);
    }

}
