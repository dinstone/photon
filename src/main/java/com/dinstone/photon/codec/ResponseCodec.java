package com.dinstone.photon.codec;

import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.serialization.SerializerType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ResponseCodec extends AbstractMessageCodec<Response> {

    private static final byte VERSION = 1;

    @Override
    public Response decode(ByteBuf in) {
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalStateException("Invalid wire version " + version + " should be <= " + VERSION);
        }
        Response response = new Response();
        // message id
        response.setMessageId(in.readInt());
        // serializer type
        response.setSerializerType(SerializerType.valueOf(in.readByte()));
        // status
        response.setStatus(Status.valueOf(in.readByte()));
        // headers

        // content
        byte[] content = readBytes(in);
        response.setContent(content);

        return response;
    }

    @Override
    public ByteBuf encode(Response message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getMessageVersion());
        out.writeInt(message.getMessageId());
        out.writeByte(message.getSerializerType().getValue());
        out.writeByte(message.getStatus().getValue());

        // headers

        // content
        byte[] content = (byte[]) message.getContent();
        writeBytes(out, content);

        return out;
    }

}
