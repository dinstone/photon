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

import com.dinstone.photon.message.Headers;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Response.Status;

import io.netty.buffer.ByteBuf;

public class ResponseCodec extends AbstractCodec<Response> {

    @Override
    public void encode(Response message, ByteBuf out) throws Exception {
        out.writeInt(message.getMsgId());
        out.writeByte(message.getCodec());
        out.writeByte(message.getStatus().getValue());

        // headers
        writeData(out, Headers.encode(message.getHeaders()));
        // content
        writeData(out, message.getContent());
    }

    @Override
    public void decode(ByteBuf in, List<Object> out) throws Exception {
        Response response = new Response();
        // message id
        response.setMsgId(in.readInt());
        // codec
        response.setCodec(in.readByte());
        // status
        response.setStatus(Status.valueOf(in.readByte()));
        // headers
        response.setHeaders(Headers.decode(readData(in)));
        // content
        response.setContent(readData(in));

        out.add(response);
    }

}
