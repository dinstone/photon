package com.dinstone.photon.codec;

import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Message.Type;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class HeatbeatCodec implements MessageCodec<Heartbeat> {

    private static final byte VERSION = 1;

    @Override
    public Heartbeat decode(ByteBuf in) {
        Type type = Message.Type.valueOf(in.readByte());
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalArgumentException("invalid message version " + version + " for " + type);
        }

        int messageId = in.readInt();
        boolean tick = in.readBoolean();
        return new Heartbeat(messageId, tick);
    }

    @Override
    public ByteBuf encode(Heartbeat message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(5);
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeBoolean(message.getTick());
        return out;
    }

}
