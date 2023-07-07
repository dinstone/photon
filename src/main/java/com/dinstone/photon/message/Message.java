/*
 * Copyright (C) 2018~2023 dinstone<dinstone@163.com>
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

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

/**
 * <pre>
 * 0  Version     Type        Reserved Flag   32
 * |----------|----------|----------/----------|
 *                  Message ID
 * |----------|----------|----------|----------|
 *                  Header Length
 * |----------|----------|----------|----------|
 *                  Header Content
 * |----------/----------/----------/----------|
 *                  Body Length
 * |----------|----------|----------|----------|
 *                  Body Content
 * |----------/----------/----------/----------|
 * </pre>
 * 
 * @author dinstone
 *
 */
public class Message {

    public enum Type {
        HEARTBEAT(0), // MEP: long connection Pattern
        REQUEST(1), // MEP: the request of the Request-Response Pattern
        RESPONSE(2), // MEP: the response of the Request-Response Pattern
        NOTICE(3); // MEP: one-way or notify Pattern

        private int value;

        private Type(int value) {
            this.value = value;
        }

        /**
         * the value to get
         *
         * @return the value
         * 
         */
        public int value() {
            return value;
        }

        public static Type valueOf(int value) {
            switch (value) {
            case 0:
                return HEARTBEAT;
            case 1:
                return REQUEST;
            case 2:
                return RESPONSE;
            case 3:
                return NOTICE;
            default:
                break;
            }
            return null;
        }

    }

    /**
     * message default version
     */
    public static final byte DEFAULT_VERSION = 1;

    protected byte version = DEFAULT_VERSION;

    protected Type type;

    protected short flag;

    protected int msgId;

    protected Headers headers;

    private byte[] hsBytes;

    protected byte[] content;

    public Message(byte version, Type type) {
        this.version = version;
        this.type = type;
        this.headers = new Headers();
    }

    public byte getVersion() {
        return version;
    }

    public Type getType() {
        return type;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public Headers headers() {
        if (hsBytes != null) {
            try {
                headers.decode(hsBytes);
            } catch (IOException e) {
                throw new DecoderException("headers decode error", e);
            }
            hsBytes = null;
        }
        return headers;
    }

    public byte[] getHeaders() {
        if (hsBytes == null) {
            try {
                hsBytes = headers.encode();
            } catch (IOException e) {
                throw new EncoderException("headers encode error", e);
            }
        }
        return hsBytes;
    }

    public void setHeaders(byte[] hsBytes) {
        this.hsBytes = hsBytes;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", flag=" + flag + ", msgId=" + msgId + ", headers=" + headers + "]";
    }

}
