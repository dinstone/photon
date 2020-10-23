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

import java.util.Map.Entry;

import com.dinstone.photon.message.Headers;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public abstract class AbstractCodec<M> implements MessageCodec<M> {

    protected void writeString(ByteBuf buff, String str) {
        byte[] strBytes = str.getBytes(CharsetUtil.UTF_8);
        buff.writeInt(strBytes.length);
        buff.writeBytes(strBytes);
    }

    protected String readString(ByteBuf in) {
        byte[] content = new byte[in.readInt()];
        in.readBytes(content);
        return new String(content, CharsetUtil.UTF_8);
    }

    protected byte[] readContent(ByteBuf in) {
        int len = in.readInt();
        if (len > 0) {
            byte[] content = new byte[len];
            in.readBytes(content);
            return content;
        }
        return null;
    }

    protected void writeContent(ByteBuf out, byte[] content) {
        if (content != null && content.length > 0) {
            out.writeInt(content.length);
            out.writeBytes(content);
        } else {
            out.writeInt(0);
        }
    }

    protected Headers readHeaders(ByteBuf in) {
        int count = in.readInt();
        if (count > 0) {
            Headers headers = new Headers();
            for (int i = 0; i < count; i++) {
                String key = readString(in);
                String value = readString(in);
                headers.put(key, value);
            }
            return headers;
        }

        return null;
    }

    protected void writeHeaders(ByteBuf out, Headers headers) {
        if (headers == null || headers.isEmpty()) {
            out.writeInt(0);
        } else {
            // count
            out.writeInt(headers.size());
            for (Entry<String, String> element : headers.entrySet()) {
                writeString(out, element.getKey());
                writeString(out, element.getValue());
            }
        }
    }

}
