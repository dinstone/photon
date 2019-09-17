/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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

package com.dinstone.photon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.connection.ResponseFuture;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class AttributeHelper {

    private static final AttributeKey<Connection> SESSION_KEY = AttributeKey.valueOf("session.key");

    private static final AttributeKey<Map<Integer, ResponseFuture>> FUTURE_KEY = AttributeKey.valueOf("future.key");

    public static void setConnection(Channel channel, Connection session) {
        channel.attr(AttributeHelper.SESSION_KEY).set(session);
    }

    public static Connection getConnection(Channel channel) {
        return channel.attr(AttributeHelper.SESSION_KEY).get();
    }

    public static Map<Integer, ResponseFuture> futureMap(Channel channel) {
        Attribute<Map<Integer, ResponseFuture>> attr = channel.attr(AttributeHelper.FUTURE_KEY);
        Map<Integer, ResponseFuture> futureMap = attr.get();
        if (futureMap == null) {
            attr.setIfAbsent(new ConcurrentHashMap<Integer, ResponseFuture>());
            futureMap = attr.get();
        }
        return futureMap;
    }

}
