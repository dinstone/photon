package com.dinstone.photon.codec;

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.serialization.SerializerType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class NoticeCodec extends AbstractCodec<Notice> {

    private static final byte VERSION = 1;

    @Override
    public Notice decode(ByteBuf in) {
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalStateException("Invalid wire version " + version + " should be <= " + VERSION);
        }
        Notice notice = new Notice();
        // message id
        notice.setMessageId(in.readInt());
        // serializer type
        notice.setSerializerType(SerializerType.valueOf(in.readByte()));
        // address
        String address = readString(in);
        notice.setAddress(address);

        // headers

        // content
        byte[] content = readBytes(in);
        notice.setContent(content);

        return notice;
    }

    @Override
    public ByteBuf encode(Notice message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getMessageVersion());
        out.writeInt(message.getMessageId());
        out.writeByte(message.getSerializerType().getValue());
        writeString(out, message.getAddress());

        // headers

        // content
        byte[] content = (byte[]) message.getContent();
        writeBytes(out, content);

        return out;
    }

}
