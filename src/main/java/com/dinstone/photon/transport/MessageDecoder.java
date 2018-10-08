package com.dinstone.photon.transport;

import java.util.List;

import com.dinstone.photon.AttributeKeys;
import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.codec.MessageCodec;
import com.dinstone.photon.protocol.Frame;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MessageDecoder extends MessageToMessageDecoder<Frame> {

	private final CodecManager codecManager;

	public MessageDecoder(CodecManager codecManager) {
		this.codecManager = codecManager;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Frame msg, List<Object> out) throws Exception {
		if (msg.isZipped()) {
			msg.dezip();
		}
		if (msg.isCrypto()) {
			msg.decrypt(ctx.channel().attr(AttributeKeys.CIPHER_KEY).get());
		}

		MessageCodec<?> messageCodec = codecManager.find(msg.getCodec());
		out.add(messageCodec.decode(msg.getDatas()));
	}

}
