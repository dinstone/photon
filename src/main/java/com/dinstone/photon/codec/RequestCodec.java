package com.dinstone.photon.codec;

import com.dinstone.photon.message.Request;
import com.dinstone.photon.serialization.SerializerType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class RequestCodec extends AbstractMessageCodec<Request> {

    private static final byte VERSION = 1;

    @Override
    public Request decode(ByteBuf in) {
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalStateException("Invalid wire version " + version + " should be <= " + VERSION);
        }
        Request request = new Request();
        // message id
        request.setMessageId(in.readInt());
        // serializer type
        request.setSerializerType(SerializerType.valueOf(in.readByte()));
        // timout
        request.setTimeout(in.readInt());
        // headers

        // content
        byte[] content = readBytes(in);
        request.setContent(content);

        return request;
    }

    @Override
    public ByteBuf encode(Request message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getMessageVersion());
        out.writeInt(message.getMessageId());
        out.writeByte(message.getSerializerType().getValue());
        out.writeInt(message.getTimeout());

        // headers

        // content
        byte[] content = (byte[]) message.getContent();
        writeBytes(out, content);

        return out;
    }

}
