package com.dinstone.photon.codec;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.message.Message.Type;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ResponseCodec extends AbstractCodec<Response> {

    private static final byte VERSION = 1;

    @Override
    public Response decode(ByteBuf in) {
        Type type = Message.Type.valueOf(in.readByte());
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalArgumentException("invalid message version " + version + " for " + type);
        }
        
        Response response = new Response();
        // message id
        response.setId(in.readInt());
        // status
        response.setStatus(Status.valueOf(in.readByte()));
        // headers
        response.setHeaders(readHeaders(in));
        // content
        byte[] content = readContent(in);
        response.setContent(content);

        return response;
    }

    @Override
    public ByteBuf encode(Response message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeByte(message.getStatus().getValue());

        // headers
        writeHeaders(out, message.getHeaders());
        // content
        writeContent(out, message.getContent());

        return out;
    }

}
