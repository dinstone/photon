/*
 * Copyright (C) 2013~2017 dinstone<dinstone@163.com>
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
package com.dinstone.photon.transport;

import java.util.List;

import com.dinstone.photon.protocol.Frame;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TransportDecoder extends ByteToMessageDecoder {

	/** 2GB */
	private int maxSize = Integer.MAX_VALUE;

	public TransportDecoder() {
	}

	public TransportDecoder(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize: " + maxSize);
		}
		this.maxSize = maxSize;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() > 4) {
			in.markReaderIndex();
			int len = in.readInt();
			if (len > maxSize) {
				throw new IllegalStateException("The encoded object is too big: " + len + " (> " + maxSize + ")");
			} else if (len < 1) {
				throw new IllegalStateException("The encoded object is too small: " + len + " (< 1)");
			}

			if (in.readableBytes() < len) {
				in.resetReaderIndex();
				return;
			}

			out.add(new Frame().decode(in.resetReaderIndex()));
		}
	}

}
