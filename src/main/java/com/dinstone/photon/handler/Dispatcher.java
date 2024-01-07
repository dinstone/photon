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
package com.dinstone.photon.handler;

import java.util.concurrent.CompletableFuture;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.Connection;
import com.dinstone.photon.MessageProcessor;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.utils.AttributeUtil;

import io.netty.channel.ChannelHandlerContext;

public class Dispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

    private final MessageProcessor processor;

    public Dispatcher(MessageProcessor processor) {
        this.processor = processor;
    }

    public void dispatch(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        switch (message.getType()) {
        case REQUEST:
            processor.process(AttributeUtil.connection(ctx.channel()), (Request) msg);
            break;
        case RESPONSE:
            handle(AttributeUtil.connection(ctx.channel()), (Response) msg);
            break;
        case HEARTBEAT:
            handle(AttributeUtil.connection(ctx.channel()), (Heartbeat) msg);
            break;
        case NOTICE:
            processor.process(AttributeUtil.connection(ctx.channel()), (Notice) msg);
            break;
        default:
            LOG.warn("unknown message : {}", msg);
            break;
        }
    }

    private void handle(Connection connection, Heartbeat msg) {
        if (msg.isPing()) {
            connection.sendMessage(msg.pong());
        }
        processor.process(connection, msg);
    }

    public void handle(Connection connection, Response msg) {
        CompletableFuture<Response> future = connection.removeFuture(msg.getSequence());
        if (future != null) {
            future.complete(msg);
        }
        processor.process(connection, msg);
    }

}
