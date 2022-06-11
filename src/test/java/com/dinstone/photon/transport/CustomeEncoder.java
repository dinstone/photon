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

import com.dinstone.photon.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomeEncoder extends MessageToByteEncoder<Message> {

    private static final byte[] PLACEHOLDER = new byte[4];

    /** 2GB */
    private int maxSize = Integer.MAX_VALUE;

    public CustomeEncoder() {
    }

    public CustomeEncoder(int maxSize) {
        if (maxSize <= 0) {
            throw new DecoderException("maxSize: " + maxSize);
        }
        this.maxSize = maxSize;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        // message encode
        message.encode(out);
    }

}
