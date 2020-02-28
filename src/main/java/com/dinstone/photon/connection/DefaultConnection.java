/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultConnection implements Connection {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConnection.class);

    private String sessionId;

    private Channel channel;

    public DefaultConnection(Channel channel) {
        this.channel = channel;
        this.sessionId = channel.id().asLongText();
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public ChannelFuture write(Message msg) {
        return channel.writeAndFlush(msg);
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void notify(Notice notice) {
        ChannelFuture cf = channel.writeAndFlush(notice);
        cf.addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOG.warn("send notice message error", future.cause());
                }
            }

        });
    }

    @Override
    public ResponseFuture async(final Request request) throws Exception {
        final ResponseFuture responseFuture = createFuture(request.getId());

        channel.writeAndFlush(request).addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    String message = "send request message error";
                    responseFuture.setResult(new RuntimeException(message, future.cause()));
                    removeFuture(responseFuture.getFutureId());
                    LOG.warn(message, future.cause());
                }
            }

        });

        return responseFuture;
    }

    @Override
    public Response sync(final Request request) throws Exception {
        return async(request).get(request.getTimeout(), TimeUnit.MILLISECONDS);
    }

    private ResponseFuture removeFuture(int messageId) {
        return AttributeHelper.futureMap(channel).remove(messageId);
    }

    private ResponseFuture createFuture(int messageId) {
        ResponseFuture future = new ResponseFuture(messageId);
        AttributeHelper.futureMap(channel).put(messageId, future);
        return future;
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
    public void destroy() {
        channel.close();
    }

}
