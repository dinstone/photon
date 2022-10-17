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

import com.dinstone.photon.Connection;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.utils.AttributeUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultConnection implements Connection {

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
        return AttributeUtil.futures(channel).remove(messageId);
    }

    @Override
    public CompletableFuture<Response> createFuture(int messageId) {
        CompletableFuture<Response> promise = new CompletableFuture<Response>();
        AttributeUtil.futures(channel).put(messageId, promise);
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
        final CompletableFuture<Response> promise = createFuture(request.getMsgId());
        channel.writeAndFlush(request).addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    removeFuture(request.getMsgId());

                    String message = "send request message error";
                    promise.completeExceptionally(new IOException(message, future.cause()));
                }
            }

        });
        return promise;
    }

}
