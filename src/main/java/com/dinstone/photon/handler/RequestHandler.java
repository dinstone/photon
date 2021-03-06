/*
 * Copyright (C) 2018~2021 dinstone<dinstone@163.com>
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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.RejectedExecutionException;

import com.dinstone.photon.ExchangeException;
import com.dinstone.photon.codec.ExceptionCodec;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Response.Status;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.processor.ProcessContext;
import com.dinstone.photon.util.ExceptionUtil;

import io.netty.channel.ChannelHandlerContext;

public class RequestHandler implements MessageHandler<Request> {

    @Override
    public void handle(final MessageProcessor processor, final ChannelHandlerContext ctx, final Request request) {
        try {
            ProcessContext context = new ProcessContext(ctx.channel());
            context.setTimeout(request.getTimeout());
            processor.process(context, request);
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = ExceptionUtil.getTargetException((InvocationTargetException) e);
            }
            ExchangeException exception = null;
            if (e instanceof RejectedExecutionException) {
                // server is busy
                exception = new ExchangeException(101, "server is busy :" + ExceptionUtil.getMessage(e));
            } else {
                exception = new ExchangeException(100, "server error :" + ExceptionUtil.getMessage(e));
            }

            Response response = new Response();
            response.setMsgId(request.getMsgId());
            response.setStatus(Status.FAILURE);
            response.setContent(ExceptionCodec.encode(exception));

            ctx.writeAndFlush(response);
        }
    }

}
