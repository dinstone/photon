package com.dinstone.photon.codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.message.MessageType;

public class CodecManager {

    private static Map<MessageType, MessageCodec<?>> codecTypeMap = new ConcurrentHashMap<>();

    static {
        regist(MessageType.HEARTBEAT, new HeatbeatCodec());
        regist(MessageType.REQUEST, new RequestCodec());
        regist(MessageType.RESPONSE, new ResponseCodec());
        regist(MessageType.NOTICE, new NoticeCodec());
    }

    public static <T> void regist(MessageType messageType, MessageCodec<T> codec) {
        if (codecTypeMap.containsKey(messageType)) {
            throw new IllegalStateException("Already a codec registered with type " + messageType);
        }
        codecTypeMap.put(messageType, codec);
    }

    @SuppressWarnings("unchecked")
    public static <T> MessageCodec<T> find(MessageType message) {
        return (MessageCodec<T>) codecTypeMap.get(message);
    }

}
