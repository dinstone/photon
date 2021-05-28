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
package com.dinstone.photon.connection;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class ConnectionManager {

    private static final ConnectionManager instance = new ConnectionManager();

    private Map<Channel, Connection> sessionMap = new ConcurrentHashMap<>();

    private ConnectionManager() {
    }

    public static void addConnection(Channel channel, Connection connection) {
        instance.sessionMap.put(channel, connection);
    }

    public static Connection delConnection(Channel channel) {
        return instance.sessionMap.remove(channel);
    }

    public static int connectionCount() {
        return instance.sessionMap.size();
    }

    public static Collection<Connection> connections() {
        return instance.sessionMap.values();
    }

}
