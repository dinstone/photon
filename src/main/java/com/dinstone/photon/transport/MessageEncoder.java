package com.dinstone.photon.transport;

import java.util.List;

import com.dinstone.photon.AttributeKeys;
import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.codec.MessageCodec;
import com.dinstone.photon.protocol.Frame;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class MessageEncoder extends MessageToMessageEncoder<Object> {

	private final CodecManager codecManager;

	public MessageEncoder(CodecManager codecManager) {
		this.codecManager = codecManager;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		MessageCodec<Object> messageCodec = (MessageCodec<Object>) codecManager.find(msg);
		Frame frame = new Frame(messageCodec.getCodecId(), messageCodec.encode(msg));
		if (ctx.channel().attr(AttributeKeys.CIPHER_KEY).get() != null) {
			frame.encrypt(ctx.channel().attr(AttributeKeys.CIPHER_KEY).get());
		}

		out.add(frame);
	}

}
