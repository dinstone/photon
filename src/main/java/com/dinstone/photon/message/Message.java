package com.dinstone.photon.message;

import com.dinstone.photon.serialization.SerializerType;

public interface Message {

    int getMessageId();

    HeaderMap getHeaders();

    Object getContent();

    SerializerType getSerializerType();

    MessageType getMessageType();

    byte getMessageVersion();

}
