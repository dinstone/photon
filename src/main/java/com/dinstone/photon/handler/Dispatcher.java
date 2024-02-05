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

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.Connection;
import com.dinstone.photon.Processor;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.utils.AttributeUtil;

import io.netty.channel.ChannelHandlerContext;

public class Dispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

    private final Processor processor;

    public Dispatcher(Processor processor) {
        this.processor = processor;
    }

    public void dispatch(ChannelHandlerContext ctx, Object msg) {
        Connection connection = AttributeUtil.connection(ctx.channel());
        switch (((Message) msg).getType()) {
        case HEARTBEAT:
            connection.dealHeartbeat((Heartbeat) msg);
            processor.process(connection, (Heartbeat) msg);
            break;
        case REQUEST:
            connection.dealRequest((Request) msg);
            processor.process(connection, (Request) msg);
            break;
        case RESPONSE:
            connection.dealResponse((Response) msg);
            processor.process(connection, (Response) msg);
            break;
        case NOTICE:
            connection.dealNotice((Notice) msg);
            processor.process(connection, (Notice) msg);
            break;
        default:
            LOG.warn("unknown message : {}", msg);
            break;
        }
    }

}
