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
package com.dinstone.photon.codec;

import java.util.List;

import com.dinstone.photon.codec.MessageDecoder.MessageState;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Message.Type;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;

public class MessageDecoder extends ReplayingDecoder<MessageState> {

    enum MessageState {
        READ_MESSAGE_TYPE, READ_MESSAGE_HEADERS, READ_MESSAGE_CONTENT
    }

    private Message message;

    public MessageDecoder() {
        super(MessageState.READ_MESSAGE_TYPE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        switch (state()) {
        case READ_MESSAGE_TYPE:
            byte version = in.readByte();
            if (Message.DEFAULT_VERSION != version) {
                throw new DecoderException("unsupported message version [" + version + "]");
            }
            byte type = in.readByte();
            short flag = in.readShort();
            int seq = in.readInt();
            message = create(type);
            message.setSequence(seq);
            message.setFlag(flag);

            checkpoint(MessageState.READ_MESSAGE_HEADERS);
            break;
        case READ_MESSAGE_HEADERS:
            // headers length
            int hlen = in.readInt();
            if (hlen > 0) {
                byte[] hs = new byte[hlen];
                in.readBytes(hs);
                message.setHeaders(hs);
            }

            checkpoint(MessageState.READ_MESSAGE_CONTENT);
            break;
        case READ_MESSAGE_CONTENT:
            // content length
            int clen = in.readInt();
            if (clen <= 0) {
                out.add(message);
                message = null;
                checkpoint(MessageState.READ_MESSAGE_TYPE);
                break;
            }

            if (in.readableBytes() >= clen) {
                byte[] cs = new byte[clen];
                in.readBytes(cs);
                message.setContent(cs);

                out.add(message);
                message = null;
                checkpoint(MessageState.READ_MESSAGE_TYPE);
            }

            break;
        default:
            // Shouldn't reach here.
            throw new Error();
        }

    }

    static Message create(byte type) {
        Type tenum = Type.valueOf(type);
        switch (tenum) {
        case HEARTBEAT:
            return new Heartbeat();
        case REQUEST:
            return new Request();
        case RESPONSE:
            return new Response();
        case NOTICE:
            return new Notice();
        default:
            break;
        }
        throw new DecoderException("unsupported message type [" + type + "]");
    }

}
