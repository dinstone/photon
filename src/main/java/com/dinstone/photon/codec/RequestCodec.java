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

import com.dinstone.photon.message.Request;

import io.netty.buffer.ByteBuf;

public class RequestCodec extends AbstractCodec<Request> {

    @Override
    public void encode(Request message, ByteBuf out) throws Exception {
        out.writeInt(message.getMsgId());
        out.writeByte(message.getCodec());
        out.writeInt(message.getTimeout());

        // headers
        writeData(out, message.getHeaders());
        // content
        writeData(out, message.getContent());
    }

    @Override
    public void decode(ByteBuf in, List<Object> out) throws Exception {
        Request request = new Request();
        // message id
        request.setMsgId(in.readInt());
        // codec
        request.setCodec(in.readByte());
        // timout
        request.setTimeout(in.readInt());
        // headers
        request.setHeaders(readData(in));
        // content
        request.setContent(readData(in));

        out.add(request);
    }

}
