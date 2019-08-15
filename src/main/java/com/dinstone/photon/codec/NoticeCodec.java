package com.dinstone.photon.codec;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Message.Type;
import com.dinstone.photon.message.Notice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class NoticeCodec extends AbstractCodec<Notice> {

    private static final byte VERSION = 1;

    @Override
    public Notice decode(ByteBuf in) {
        Type type = Message.Type.valueOf(in.readByte());
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalArgumentException("invalid message version " + version + " for " + type);
        }
        
        Notice notice = new Notice();
        // message id
        notice.setId(in.readInt());
        // address
        String address = readString(in);
        notice.setAddress(address);

        // headers
        notice.setHeaders(readHeaders(in));
        // content
        byte[] content = readContent(in);
        notice.setContent(content);

        return notice;
    }

    @Override
    public ByteBuf encode(Notice message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        writeString(out, message.getAddress());

        // headers
        writeHeaders(out, message.getHeaders());
        // content
        writeContent(out, message.getContent());

        return out;
    }

}
