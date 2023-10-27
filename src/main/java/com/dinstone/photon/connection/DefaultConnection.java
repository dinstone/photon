/*
 * Copyright (C) 2018~2023 dinstone<dinstone@163.com>
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

    private Map<Integer, CompletableFuture<Response>> reponseFutures = new ConcurrentHashMap<>();
    private Map<Integer, ScheduledFuture<?>> timeoutFutures = new ConcurrentHashMap<>();

    private Channel channel;

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
        reponseFutures.forEach((id, rf) -> rf.cancel(false));
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
    public CompletableFuture<Response> removeFuture(int messageId) {
        ScheduledFuture<?> tf = timeoutFutures.remove(messageId);
        if (tf != null) {
            tf.cancel(false);
        }
        return reponseFutures.remove(messageId);
    }

    @Override
    public CompletableFuture<Response> createFuture(Request request) {
        CompletableFuture<Response> promise = new CompletableFuture<Response>();
        reponseFutures.put(request.getMsgId(), promise);
        ScheduledFuture<?> tf = channel.eventLoop().schedule(new Runnable() {

            @Override
            public void run() {
                CompletableFuture<Response> future = removeFuture(request.getMsgId());
                if (future != null) {
                    future.completeExceptionally(
                            new TimeoutException("request timeout of " + request.getTimeout() + "ms"));
                }
            }
        }, request.getTimeout(), TimeUnit.MILLISECONDS);
        timeoutFutures.put(request.getMsgId(), tf);
        return promise;
    }

    @Override
    public CompletableFuture<Void> sendMessage(Message msg) {
        CompletableFuture<Void> promise = new CompletableFuture<Void>();
        channel.writeAndFlush(msg).addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    promise.completeExceptionally(new IOException("send message error", future.cause()));
                }
            }

        });
        return promise;
    }

    @Override
    public CompletableFuture<Response> sendRequest(Request request) throws Exception {
        final CompletableFuture<Response> promise = createFuture(request);
        channel.writeAndFlush(request).addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    removeFuture(request.getMsgId());

                    promise.completeExceptionally(future.cause());
                }
            }

        });
        return promise;
    }

}
