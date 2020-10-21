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
package com.dinstone.photon.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

public class HandlerManager {

    private static Map<Class<?>, MessageHandler<?>> handlers = new ConcurrentHashMap<>();

    static {
        regist(Notice.class, new NoticeHandler());
        regist(Request.class, new RequestHandler());
        regist(Response.class, new ResponseHandler());
        regist(Heartbeat.class, new HeartbeatHandler());
    }

    public static <T> void regist(Class<T> messageType, MessageHandler<T> messageHandler) {
        if (handlers.containsKey(messageType)) {
            throw new IllegalStateException("already a handler registered with type " + messageType);
        }
        handlers.put(messageType, messageHandler);
    }

    @SuppressWarnings("unchecked")
    public static <T> MessageHandler<Object> find(Class<T> messageType) {
        return (MessageHandler<Object>) handlers.get(messageType);
    }

}
