/*
 * Copyright (C) 2018~2020 dinstone<dinstone@163.com>
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

import java.util.concurrent.Executor;

import com.dinstone.photon.codec.ExceptionCodec;
import com.dinstone.photon.connection.ResponseFuture;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.util.AttributeHelper;

import io.netty.channel.ChannelHandlerContext;

public class ResponseHandler implements MessageHandler<Response> {

    @Override
    public void handle(Executor executor, MessageProcessor processor, ChannelHandlerContext ctx,
            final Response response) {
        final ResponseFuture future = AttributeHelper.futures(ctx.channel()).remove(response.getMsgId());
        if (future != null) {
            if (executor != null) {
                executor.execute(new Runnable() {

                    @Override
                    public void run() {
                        resolveResponse(future, response);
                    }

                });
            } else {
                resolveResponse(future, response);
            }
        }
    }

    private void resolveResponse(final ResponseFuture future, final Response response) {
        Status status = response.getStatus();
        if (status == Status.SUCCESS) {
            future.setResponse(response);
        } else {
            future.setFailure(ExceptionCodec.decode(response.getContent()));
        }
    }
}
