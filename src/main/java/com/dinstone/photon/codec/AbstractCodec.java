package com.dinstone.photon.codec;

import java.util.Map.Entry;

import com.dinstone.photon.message.Headers;
import com.dinstone.photon.message.Message;

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

    protected byte[] readContent(ByteBuf in) {
        int len = in.readInt();
        if (len > 0) {
            byte[] content = new byte[len];
            in.readBytes(content);
            return content;
        }
        return null;
    }

    protected void writeContent(ByteBuf out, byte[] content) {
        if (content != null && content.length > 0) {
            out.writeInt(content.length);
            out.writeBytes(content);
        } else {
            out.writeInt(0);
        }
    }

    protected Headers readHeaders(ByteBuf in) {
        int count = in.readInt();
        if (count > 0) {
            Headers headers = new Headers();
            for (int i = 0; i < count; i++) {
                String key = readString(in);
                String value = readString(in);
                headers.put(key, value);
            }
            return headers;
        }

        return null;
    }

    protected void writeHeaders(ByteBuf out, Headers headers) {
        if (headers == null || headers.isEmpty()) {
            out.writeInt(0);
        } else {
            // count
            out.writeInt(headers.size());
            for (Entry<String, String> element : headers.entrySet()) {
                writeString(out, element.getKey());
                writeString(out, element.getValue());
            }
        }
    }

    public static ByteBuf encodeMessage(Message message) {
        MessageCodec<Message> codec = CodecManager.find(message.getType());
        if (codec != null) {
            return codec.encode(message);
        } else {
            throw new IllegalStateException("can't find message codec for " + message.getType());
        }
    }

    public static Message decodeMessage(ByteBuf in) {
        in.markReaderIndex();
        Message.Type messageType = Message.Type.valueOf(in.readByte());
        MessageCodec<Message> codec = CodecManager.find(messageType);
        if (codec != null) {
            in.resetReaderIndex();
            return codec.decode(in);
        } else {
            throw new IllegalStateException("can't find message codec for " + messageType);
        }
    }
}
