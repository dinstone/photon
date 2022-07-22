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
package com.dinstone.photon.handler;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.MessageProcessor;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.utils.AttributeUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;

public class MessageHandleDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(MessageHandleDispatcher.class);

    private MessageProcessor processor;

    public MessageHandleDispatcher(MessageProcessor processor) {
        this.processor = processor;
    }

    public void dispatch(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Request) {
            handle(ctx, (Request) msg);
        } else if (msg instanceof Response) {
            handle(ctx, (Response) msg);
        } else if (msg instanceof Heartbeat) {
            handle(ctx, (Heartbeat) msg);
        } else if (msg instanceof Notice) {
            handle(ctx, (Notice) msg);
        } else {
            LOG.warn("unkown message : {}", msg);
        }

    }

    public void handle(final ChannelHandlerContext ctx, final Request request) {
        processor.process(AttributeUtil.connection(ctx.channel()), request);
    }

    public void handle(ChannelHandlerContext ctx, final Response response) {
        Promise<Response> promise = AttributeUtil.promises(ctx.channel()).remove(response.getMsgId());
        if (promise != null) {
            promise.setSuccess(response);
        }
        processor.process(AttributeUtil.connection(ctx.channel()), response);
    }

    public void handle(final ChannelHandlerContext ctx, final Notice msg) {
        processor.process(AttributeUtil.connection(ctx.channel()), msg);
    }

    public void handle(ChannelHandlerContext ctx, Heartbeat heartbeat) {
        if (heartbeat.isPing()) {
            ctx.writeAndFlush(heartbeat.pong());
        }
        processor.process(AttributeUtil.connection(ctx.channel()), heartbeat);
    }

}
