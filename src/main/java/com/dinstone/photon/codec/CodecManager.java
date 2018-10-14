package com.dinstone.photon.codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.photon.protocol.Agreement;
import com.dinstone.photon.protocol.Heartbeat;

public class CodecManager {

	private static final CodecManager instance = new CodecManager();
	private Map<Byte, MessageCodec<?>> codecIdMap = new ConcurrentHashMap<>();
	private Map<Class<?>, MessageCodec<?>> codecTypeMap = new ConcurrentHashMap<>();

	private CodecManager() {
		regist(Heartbeat.class, new HeatbeatCodec());
		regist(Agreement.class, new AgreementCodec());
	}

	public <T> void regist(Class<T> clazz, MessageCodec<T> codec) {
		if (codecIdMap.containsKey(codec.getCodecId())) {
			throw new IllegalStateException("Already a codec registered for id " + codec.getCodecId());
		}
		if (codecTypeMap.containsKey(clazz)) {
			throw new IllegalStateException("Already a codec registered with class " + clazz);
		}
		codecIdMap.put(codec.getCodecId(), codec);
		codecTypeMap.put(clazz, codec);
	}

	public MessageCodec<?> find(byte codecId) {
		return codecIdMap.get(codecId);
	}

	public <T> MessageCodec<T> find(T message) {
		return (MessageCodec<T>) codecTypeMap.get(message.getClass());
	}

	public static CodecManager getInstance() {
		return instance;
	}
}
