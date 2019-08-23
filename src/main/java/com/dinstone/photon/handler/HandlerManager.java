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
