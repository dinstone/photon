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
package com.dinstone.photon.transport;

import java.util.List;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.transport.CustomeDecoder.CustomeState;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class CustomeDecoder extends ReplayingDecoder<CustomeState> {

    public enum CustomeState {
        read_message_type, read_message_content
    }

    private Message message;

    public CustomeDecoder() {
        super(CustomeState.read_message_type);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
        case read_message_type:
            // mark
            in.markReaderIndex();
            byte version = in.readByte();
            byte type = in.readByte();
            // reset
            in.resetReaderIndex();

            message = Message.create(version, type);
            checkpoint(CustomeState.read_message_content);

            break;
        case read_message_content:
            message.decode(in);
            out.add(message);
            checkpoint(CustomeState.read_message_type);

            message = null;

            break;
        default:
            // Shouldn't reach here.
            throw new Error();
        }

    }

}
