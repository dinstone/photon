/*
 * Copyright (C) 2018~2021 dinstone<dinstone@163.com>
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

import java.util.List;

import com.dinstone.photon.message.Heartbeat;

import io.netty.buffer.ByteBuf;

public class HeatbeatCodec extends AbstractCodec<Heartbeat> {

    @Override
    public void encode(Heartbeat message, ByteBuf out) {
        out.writeInt(message.getMsgId());
        out.writeBoolean(message.getTick());
    }

    @Override
    public void decode(ByteBuf in, List<Object> out) {
        int messageId = in.readInt();
        boolean tick = in.readBoolean();
        out.add(new Heartbeat(messageId, tick));
    }

}
