/*
 * Copyright (C) 2018~2023 dinstone<dinstone@163.com>
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

import com.dinstone.photon.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message> {

    public MessageEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        // message version
        out.writeByte(message.getVersion());
        // message type
        out.writeByte(message.getType().value());
        // message flag
        out.writeShort(message.getFlag());
        // message id
        out.writeInt(message.getMsgId());

        // headers
        byte[] h = message.getHeaders();
        if (h != null && h.length > 0) {
            out.writeInt(h.length);
            out.writeBytes(h);
        } else {
            out.writeInt(0);
        }

        // content
        byte[] c = message.getContent();
        if (c != null && c.length > 0) {
            out.writeInt(c.length);
            out.writeBytes(c);
        } else {
            out.writeInt(0);
        }
    }

}
