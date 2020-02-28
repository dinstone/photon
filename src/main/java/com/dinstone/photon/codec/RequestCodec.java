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

import java.util.List;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Message.Type;
import com.dinstone.photon.message.Request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class RequestCodec extends AbstractCodec<Request> {

    private static final byte VERSION = 1;

    @Override
    public Request decode(ByteBuf in) {
        Type type = Message.Type.valueOf(in.readByte());
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalArgumentException("invalid message version " + version + " for " + type);
        }

        Request request = new Request();
        // message id
        request.setId(in.readInt());
        // timout
        request.setTimeout(in.readInt());
        // headers
        request.setHeaders(readHeaders(in));

        // content
        byte[] content = readContent(in);
        request.setContent(content);

        return request;
    }

    @Override
    public ByteBuf encode(Request message) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(32);
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeInt(message.getTimeout());

        // headers
        writeHeaders(out, message.getHeaders());
        // content
        writeContent(out, message.getContent());

        return out;
    }

    @Override
    public void encode(Request message, ByteBuf out) {
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeInt(message.getTimeout());

        // headers
        writeHeaders(out, message.getHeaders());
        // content
        writeContent(out, message.getContent());
    }

    @Override
    public void decode(ByteBuf in, List<Object> out) {
        Type type = Message.Type.valueOf(in.readByte());
        byte version = in.readByte();
        if (VERSION != version) {
            throw new IllegalArgumentException("invalid message version " + version + " for " + type);
        }

        Request request = new Request();
        // message id
        request.setId(in.readInt());
        // timout
        request.setTimeout(in.readInt());
        // headers
        request.setHeaders(readHeaders(in));

        // content
        byte[] content = readContent(in);
        request.setContent(content);

        out.add(request);
    }

}
