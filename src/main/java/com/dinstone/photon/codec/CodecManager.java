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
