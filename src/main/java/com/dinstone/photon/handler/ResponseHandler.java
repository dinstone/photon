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

import com.dinstone.photon.MessageProcessor;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.utils.AttributeUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;

public class ResponseHandler implements MessageHandler<Response> {

    @Override
    public void handle(MessageProcessor processor, ChannelHandlerContext ctx, final Response response) {
        Promise<Response> promise = AttributeUtil.promises(ctx.channel()).remove(response.getMsgId());
        if (promise != null) {
            promise.setSuccess(response);
        }
    }

}
