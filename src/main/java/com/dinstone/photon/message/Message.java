/*
 * Copyright (C) 2018~2022 dinstone<dinstone@163.com>
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

public class Message {

    public enum Type {
        HEARTBEAT(0), // MEP: long connection parttern
        REQUEST(1), // MEP: the request of the request-respose parttern
        RESPONSE(2), // MEP: the response of the request-respose parttern
        NOTICE(3); // MEP: one-way or notify parttern

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
            throw new IllegalArgumentException("unsupported message type [" + value + "]");
        }

    }

    /**
     * message default version
     */
    public static final byte DEFAULT_VERSION = 1;

    protected byte version = DEFAULT_VERSION;

    protected Type type;

    protected int msgId;

    protected Headers headers;

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

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public Headers headers() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message [version=" + version + ", type=" + type + ", msgId=" + msgId + ", headers=" + headers + "]";
    }

}
