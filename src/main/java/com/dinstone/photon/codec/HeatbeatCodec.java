package com.dinstone.photon.codec;

import com.dinstone.photon.message.Heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class HeatbeatCodec implements MessageCodec<Heartbeat> {

    private static final byte VERSION = 1;

    @Override
    public Heartbeat decode(ByteBuf in) {
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalStateException("Invalid wire version " + version + " should be <= " + VERSION);
        }
        int messageId = in.readInt();
        return new Heartbeat(messageId);
    }

    @Override
    public ByteBuf encode(Heartbeat message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(5);
        out.writeByte(message.getMessageVersion());
        out.writeInt(message.getMessageId());
        return out;
    }

}
