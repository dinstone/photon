/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
