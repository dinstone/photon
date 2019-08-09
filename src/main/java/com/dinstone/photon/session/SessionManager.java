package com.dinstone.photon.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class SessionManager {

    private static final SessionManager instance = new SessionManager();

    private Map<Channel, Session> sessionMap = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void addSession(Channel channel, Session session) {
        instance.sessionMap.put(channel, session);
    }

    public static Session delSession(Channel channel) {
        return instance.sessionMap.remove(channel);
    }

    public static int sessionCount() {
        return instance.sessionMap.size();
    }

}
