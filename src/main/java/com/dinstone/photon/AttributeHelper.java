
package com.dinstone.photon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.session.ResponseFuture;
import com.dinstone.photon.session.Session;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class AttributeHelper {

    private static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("session.key");

    private static final AttributeKey<Map<Integer, ResponseFuture>> FUTURE_KEY = AttributeKey.valueOf("future.key");

    public static void setSession(Channel channel, Session session) {
        channel.attr(AttributeHelper.SESSION_KEY).set(session);
    }

    public static Session getSession(Channel channel) {
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
