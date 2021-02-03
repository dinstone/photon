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
package com.dinstone.photon.connection;

import java.net.InetSocketAddress;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

import io.netty.channel.ChannelFuture;

public interface Connection {

    String sessionId();

    boolean isBusy();

    boolean isActive();

    public void destroy();

    public InetSocketAddress getRemoteAddress();

    public InetSocketAddress getLocalAddress();

    void notify(Notice notice);

    ChannelFuture write(Message message);

    Response sync(Request request) throws Exception;

    ResponseFuture async(Request request) throws Exception;

}
