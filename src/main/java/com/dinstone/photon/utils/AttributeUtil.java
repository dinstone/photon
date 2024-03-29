/*
 * Copyright (C) 2018~2024 dinstone<dinstone@163.com>
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
package com.dinstone.photon.utils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.Connection;
import com.dinstone.photon.message.Response;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class AttributeUtil {

    private static final AttributeKey<Connection> CONNECTION_KEY = AttributeKey.valueOf("connection.key");

    private static final AttributeKey<Map<Integer, CompletableFuture<Response>>> FUTURE_KEY = AttributeKey
            .valueOf("future.key");

    public static void connection(Channel channel, Connection connection) {
        channel.attr(AttributeUtil.CONNECTION_KEY).set(connection);
    }

    public static Connection connection(Channel channel) {
        return channel.attr(AttributeUtil.CONNECTION_KEY).get();
    }

    public static Map<Integer, CompletableFuture<Response>> futures(Channel channel) {
        Attribute<Map<Integer, CompletableFuture<Response>>> attr = channel.attr(AttributeUtil.FUTURE_KEY);
        Map<Integer, CompletableFuture<Response>> futures = attr.get();
        if (futures == null) {
            attr.setIfAbsent(new ConcurrentHashMap<>());
            futures = attr.get();
        }
        return futures;
    }

}
