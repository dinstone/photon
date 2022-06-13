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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

public class TransportDecoder extends ByteToMessageDecoder {

    /** 2GB */
    private int maxSize = Integer.MAX_VALUE;

    public TransportDecoder() {
    }

    public TransportDecoder(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0 :" + maxSize);
        }
        this.maxSize = maxSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() > 4) {
            in.markReaderIndex();
            int length = in.readInt();
            if (length > maxSize) {
                throw new DecoderException("encoded data is too big: " + length + " (> " + maxSize + ")");
            } else if (length < 1) {
                throw new DecoderException("encoded data is too small: " + length + " (<1)");
            }
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            // mark message start index
            in.markReaderIndex();
            byte version = in.readByte();
            byte type = in.readByte();
            Message message = Message.create(version, type);

            // reset message start index
            in.resetReaderIndex();
            message.decode(in);
            out.add(message);
        }
    }

}
