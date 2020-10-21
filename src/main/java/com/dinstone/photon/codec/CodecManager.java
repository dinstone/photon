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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.message.Message;

public class CodecManager {

    private static Map<Message.Type, MessageCodec<?>> codecTypeMap = new ConcurrentHashMap<>();

    static {
        regist(Message.Type.HEARTBEAT, new HeatbeatCodec());
        regist(Message.Type.REQUEST, new RequestCodec());
        regist(Message.Type.RESPONSE, new ResponseCodec());
        regist(Message.Type.NOTICE, new NoticeCodec());
    }

    public static <T> void regist(Message.Type messageType, MessageCodec<T> codec) {
        if (codecTypeMap.containsKey(messageType)) {
            throw new IllegalStateException("already a codec registered with type " + messageType);
        }
        codecTypeMap.put(messageType, codec);
    }

    @SuppressWarnings("unchecked")
    public static <T> MessageCodec<T> find(Message.Type message) {
        return (MessageCodec<T>) codecTypeMap.get(message);
    }

}
