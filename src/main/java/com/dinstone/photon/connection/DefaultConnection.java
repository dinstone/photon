/*
 * Copyright (C) 2018~2024 dinstone<dinstone@163.com>
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.dinstone.photon.Connection;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

public class DefaultConnection implements Connection {

    private final Map<Integer, CompletableFuture<Response>> responseFutures = new ConcurrentHashMap<>();

    private final Map<Integer, ScheduledFuture<?>> timeoutFutures = new ConcurrentHashMap<>();

    private final Channel channel;

    public DefaultConnection(Channel channel) {
        this.channel = channel;
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
        responseFutures.forEach((id, rf) -> rf.cancel(false));
        timeoutFutures.forEach((id, sf) -> sf.cancel(false));
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
    public CompletableFuture<Response> removeFuture(int sequence) {
        ScheduledFuture<?> tf = timeoutFutures.remove(sequence);
        if (tf != null) {
            tf.cancel(false);
        }
        return responseFutures.remove(sequence);
    }

    @Override
    public CompletableFuture<Response> createFuture(Request request) {
        CompletableFuture<Response> promise = new CompletableFuture<>();
        responseFutures.put(request.getSequence(), promise);
        ScheduledFuture<?> tf = channel.eventLoop().schedule(() -> {
            CompletableFuture<Response> future = removeFuture(request.getSequence());
            if (future != null) {
                future.completeExceptionally(new TimeoutException("request timeout of " + request.getTimeout() + "ms"));
            }
        }, request.getTimeout(), TimeUnit.MILLISECONDS);
        timeoutFutures.put(request.getSequence(), tf);
        return promise;
    }

    @Override
    public CompletableFuture<Void> sendMessage(Message msg) {
        CompletableFuture<Void> promise = new CompletableFuture<>();
        channel.writeAndFlush(msg).addListener((GenericFutureListener<ChannelFuture>) future -> {
            if (!future.isSuccess()) {
                promise.completeExceptionally(new IOException("send message error", future.cause()));
            } else {
                promise.complete(null);
            }
        });
        return promise;
    }

    @Override
    public CompletableFuture<Response> sendRequest(Request request) {
        final CompletableFuture<Response> promise = createFuture(request);
        channel.writeAndFlush(request).addListener((GenericFutureListener<ChannelFuture>) future -> {
            if (!future.isSuccess()) {
                removeFuture(request.getSequence());

                promise.completeExceptionally(future.cause());
            }
        });
        return promise;
    }

}
