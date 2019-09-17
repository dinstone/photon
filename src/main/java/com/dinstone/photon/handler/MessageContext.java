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
package com.dinstone.photon.handler;

import java.util.Map;
import java.util.concurrent.Executor;

import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.connection.ResponseFuture;

import io.netty.channel.ChannelHandlerContext;

public class MessageContext {

    private ChannelHandlerContext channelContext;

    private Executor defaultExecutor;

    public MessageContext(ChannelHandlerContext channelContext, Executor defaultExecutor) {
        this.channelContext = channelContext;
        this.defaultExecutor = defaultExecutor;
    }

    public ChannelHandlerContext getChannelContext() {
        return channelContext;
    }

    public Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    public Connection getConnection() {
        return AttributeHelper.getConnection(channelContext.channel());
    }

    public Map<Integer, ResponseFuture> getResponseFutures() {
        return AttributeHelper.futureMap(channelContext.channel());
    }

}
