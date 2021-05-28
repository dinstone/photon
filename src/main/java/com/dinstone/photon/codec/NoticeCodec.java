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
import com.dinstone.photon.message.Notice;

import io.netty.buffer.ByteBuf;

public class NoticeCodec extends AbstractCodec<Notice> {

    @Override
    public void encode(Notice message, ByteBuf out) throws Exception {
        out.writeInt(message.getMsgId());
        out.writeByte(message.getCodec());
        writeString(out, message.getAddress());

        // headers
        writeData(out, Headers.encode(message.getHeaders()));
        // content
        writeData(out, message.getContent());
    }

    @Override
    public void decode(ByteBuf in, List<Object> out) throws Exception {
        Notice notice = new Notice();
        // message id
        notice.setMsgId(in.readInt());
        notice.setCodec(in.readByte());
        // address
        String address = readString(in);
        notice.setAddress(address);
        // headers
        notice.setHeaders(Headers.decode(readData(in)));
        // content
        notice.setContent(readData(in));

        out.add(notice);
    }

}
