package com.dinstone.photon.codec;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Message.Type;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class RequestCodec extends AbstractCodec<Request> {

    private static final byte VERSION = 1;

    @Override
    public Request decode(ByteBuf in) {
        Type type = Message.Type.valueOf(in.readByte());
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalArgumentException("invalid message version " + version + " for " + type);
        }
        
        Request request = new Request();
        // message id
        request.setId(in.readInt());
        // timout
        request.setTimeout(in.readInt());
        // headers
        request.setHeaders(readHeaders(in));

        // content
        byte[] content = readContent(in);
        request.setContent(content);

        return request;
    }

    @Override
    public ByteBuf encode(Request message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeInt(message.getTimeout());

        // headers
        writeHeaders(out, message.getHeaders());
        // content
        writeContent(out, message.getContent());

        return out;
    }

}
