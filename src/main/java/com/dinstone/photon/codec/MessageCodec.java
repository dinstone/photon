package com.dinstone.photon.codec;

import io.netty.buffer.ByteBuf;

public interface MessageCodec<M> {

    M decode(ByteBuf in);

    ByteBuf encode(M message);

}
