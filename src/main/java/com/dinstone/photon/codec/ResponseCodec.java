/*
 * Copyright (C) 2018~2020 dinstone<dinstone@163.com>
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
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;

import io.netty.buffer.ByteBuf;

public class ResponseCodec extends AbstractCodec<Response> {

    private static final byte VERSION = 1;

    @Override
    public void encode(Response message, ByteBuf out) {
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeByte(message.getStatus().getValue());

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

        Response response = new Response();
        // message id
        response.setId(in.readInt());
        // status
        response.setStatus(Status.valueOf(in.readByte()));
        // headers
        response.setHeaders(readHeaders(in));
        // content
        byte[] content = readContent(in);
        response.setContent(content);

        out.add(response);
    }

}
