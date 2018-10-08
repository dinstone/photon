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

import com.dinstone.photon.protocol.Frame;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TransportEncoder extends MessageToByteEncoder<Frame> {

	/** 2GB */
	private int maxSize = Integer.MAX_VALUE;

	public TransportEncoder() {
	}

	public TransportEncoder(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize: " + maxSize);
		}
		this.maxSize = maxSize;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Frame frame, ByteBuf out) throws Exception {
		int length = frame.getDatas().length;
		if (length > maxSize) {
			throw new IllegalArgumentException("The encoded object is too big: " + length + " (> " + maxSize + ')');
		}

		frame.encode(out);
	}

}
