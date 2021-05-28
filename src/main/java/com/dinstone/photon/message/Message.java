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

import io.netty.buffer.ByteBuf;

public interface Message {

    /**
     * MEP: long connection parttern
     */
    byte HEARTBEAT = 0;

    /**
     * MEP: the request of the request-respose parttern
     */
    byte REQUEST = 1;

    /**
     * MEP: the response of the request-respose parttern
     */
    byte RESPONSE = 2;

    /**
     * MEP: one-way or notify parttern
     */
    byte NOTICE = 3;

    byte getVersion();

    byte getType();

    int getMsgId();

    void encode(ByteBuf oBuffer) throws Exception;

    void decode(ByteBuf iBuffer) throws Exception;

    static Message create(byte version, byte type) {
        switch (type) {
        case 0:
            return new Heartbeat();
        case 1:
            return new Request();
        case 2:
            return new Response();
        case 3:
            return new Notice();
        default:
            break;
        }
        throw new IllegalArgumentException("unsupported message type [" + type + "]");
    }

}
