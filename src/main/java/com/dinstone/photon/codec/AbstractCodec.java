package com.dinstone.photon.codec;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public abstract class AbstractCodec<M> implements MessageCodec<M> {

    protected void writeString(ByteBuf buff, String str) {
        byte[] strBytes = str.getBytes(CharsetUtil.UTF_8);
        buff.writeInt(strBytes.length);
        buff.writeBytes(strBytes);
    }

    protected String readString(ByteBuf in) {
        byte[] content = new byte[in.readInt()];
        in.readBytes(content);
        return new String(content, CharsetUtil.UTF_8);
    }

    protected byte[] readBytes(ByteBuf in) {
        int len = in.readInt();
        if (len > 0) {
            byte[] content = new byte[len];
            in.readBytes(content);
            return content;
        }
        return null;
    }

    protected void writeBytes(ByteBuf out, byte[] content) {
        if (content != null && content.length > 0) {
            out.writeInt(content.length);
            out.writeBytes(content);
        } else {
            out.writeInt(0);
        }
    }
}
