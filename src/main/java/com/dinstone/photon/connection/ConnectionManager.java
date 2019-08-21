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
