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
package com.dinstone.photon.message;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public abstract class AbstractMessage implements Message {

    private static final byte DEFAULT_VERSION = 1;

    protected byte version = DEFAULT_VERSION;

    protected byte type;

    protected int msgId;

    protected Headers headers;

    protected byte[] content;

    public AbstractMessage(byte type) {
        super();
        this.type = type;
    }

    public byte getVersion() {
        return version;
    }

    public byte getType() {
        return type;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public Headers headers() {
        if (headers == null) {
            headers = new Headers();
        }
        return headers;
    }

    public byte[] getHeaders() {
        try {
            return Headers.encode(headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHeaders(byte[] headers) {
        try {
            if (headers != null) {
                Headers hs = Headers.decode(headers);
                if (hs != null) {
                    this.headers = hs;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    protected void writeString(ByteBuf buff, String str) {
        if (str != null) {
            byte[] strBytes = str.getBytes(CharsetUtil.UTF_8);
            buff.writeInt(strBytes.length);
            buff.writeBytes(strBytes);
        } else {
            buff.writeInt(0);
        }
    }

    protected String readString(ByteBuf in) {
        byte[] content = new byte[in.readInt()];
        in.readBytes(content);
        return new String(content, CharsetUtil.UTF_8);
    }

    protected byte[] readData(ByteBuf in) {
        int len = in.readInt();
        if (len > 0) {
            byte[] content = new byte[len];
            in.readBytes(content);
            return content;
        }
        return null;
    }

    protected void writeData(ByteBuf out, byte[] data) {
        if (data != null && data.length > 0) {
            out.writeInt(data.length);
            out.writeBytes(data);
        } else {
            out.writeInt(0);
        }
    }

    @Override
    public void encode(ByteBuf oBuffer) throws Exception {
        // message version
        oBuffer.writeByte(version);
        // message type
        oBuffer.writeByte(type);
        // message id
        oBuffer.writeInt(msgId);
    }

    @Override
    public void decode(ByteBuf iBuffer) throws Exception {
        byte version = iBuffer.readByte();
        if (this.version != version) {
            throw new IllegalStateException("invalid message version " + version);
        }
        byte type = iBuffer.readByte();
        if (this.type != type) {
            throw new IllegalStateException("invalid message type " + type);
        }
        msgId = iBuffer.readInt();
    }

}
