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

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.processor.ProcessContext;

import io.netty.channel.ChannelHandlerContext;

public class NoticeHandler implements MessageHandler<Notice> {

    @Override
    public void handle(final MessageProcessor processor, final ChannelHandlerContext ctx, final Notice msg) {
        processor.process(new ProcessContext(ctx.channel()), msg);
    }

}
