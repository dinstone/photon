/*
 * Copyright (C) 2018~2022 dinstone<dinstone@163.com>
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
package com.dinstone.photon.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.Connection;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.utils.AttributeUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public class DefaultConnection implements Connection {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultConnection.class);

	private Channel channel;

	public DefaultConnection(Channel channel) {
		this.channel = channel;
	}

	@Override
	public Channel channel() {
		return channel;
	}

	@Override
	public String connectionId() {
		return channel.id().asLongText();
	}

	@Override
	public boolean isActive() {
		return channel.isActive();
	}

	@Override
	public boolean isBusy() {
		return !channel.isWritable();
	}

	@Override
	public void destroy() {
		channel.close();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) channel.remoteAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return (InetSocketAddress) channel.localAddress();
	}

	@Override
	public ChannelFuture send(Message msg) {
		return channel.writeAndFlush(msg);
	}

	@Override
	public Response sync(final Request request) throws Exception {
		try {
			return async(request).get(request.getTimeout(), TimeUnit.MILLISECONDS);
		} finally {
			removeFuture(request.getMsgId());
		}
	}

	@Override
	public Future<Response> async(final Request request) throws Exception {
		final Promise<Response> promise = createFuture(request.getMsgId());
		channel.writeAndFlush(request).addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					String message = "send request message error";
					promise.setFailure(new IOException(message, future.cause()));
					removeFuture(request.getMsgId());
					LOG.warn(message, future.cause());
				}
			}

		});
		return promise;
	}

	private Promise<Response> removeFuture(int messageId) {
		return AttributeUtil.promises(channel).remove(messageId);
	}

	private Promise<Response> createFuture(int messageId) {
		Promise<Response> promise = new DefaultPromise<Response>(channel.eventLoop());
		AttributeUtil.promises(channel).put(messageId, promise);
		return promise;
	}

	@Override
	public CompletableFuture<Response> asyncRequest(Request request) throws Exception {
		final CompletableFuture<Response> promise = new CompletableFuture<>();
		AttributeUtil.futures(channel).put(request.getMsgId(), promise);
		channel.writeAndFlush(request).addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					String message = "send request message error";
					promise.completeExceptionally(new IOException(message, future.cause()));
					AttributeUtil.futures(channel).remove(request.getMsgId());
					LOG.warn(message, future.cause());
				}
			}

		});
		return promise;
	}

}
