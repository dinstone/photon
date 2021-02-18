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
package com.dinstone.photon.processor;

import java.net.InetSocketAddress;

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Response;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ProcessContext {

    private int timeout;

    private long arriveTime;

    private Channel channel;

    public ProcessContext(Channel channel) {
        this.channel = channel;
        this.arriveTime = System.currentTimeMillis();
    }

    public ChannelFuture send(Notice notice) {
        return channel.writeAndFlush(notice);
    }

    public ChannelFuture send(Response response) {
        return channel.writeAndFlush(response);
    }

    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    public ProcessContext setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public boolean isTimeout() {
        return (timeout > 0 && arriveTime - System.currentTimeMillis() >= timeout);
    }

}
