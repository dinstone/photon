/*
 * Copyright (C) 2018~2024 dinstone<dinstone@163.com>
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

import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

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

            byte version = in.readByte();
            if (Message.DEFAULT_VERSION != version) {
                throw new DecoderException("unsupported message version [" + version + "]");
            }
            byte type = in.readByte();
            short flag = in.readShort();
            int seq = in.readInt();
            Message message = create(type);
            message.setSequence(seq);
            message.setFlag(flag);

            // headers length
            int hlen = in.readInt();
            if (hlen > 0) {
                byte[] hs = new byte[hlen];
                in.readBytes(hs);
                message.setHeaders(hs);
            }

            // content length
            int clen = in.readInt();
            if (clen > 0) {
                byte[] cs = new byte[clen];
                in.readBytes(cs);
                message.setContent(cs);
            }

            out.add(message);
        }
    }

    static Message create(byte type) {
        switch (type) {
        case 0:
            return new Heartbeat();
        case 1:
            return new Request();
        case 2:
            return new Response();
        case 3:
            return new Notice();
        default:
            break;
        }
        throw new DecoderException("unsupported message type [" + type + "]");
    }

}
