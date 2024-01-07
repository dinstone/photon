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
package com.dinstone.photon;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

public interface Connection {

    String connectionId();

    boolean isBusy();

    boolean isActive();

    void destroy();

    InetSocketAddress getRemoteAddress();

    InetSocketAddress getLocalAddress();

    CompletableFuture<Void> sendMessage(Message message);

    CompletableFuture<Response> sendRequest(Request request) throws Exception;

    CompletableFuture<Response> createFuture(Request request);

    CompletableFuture<Response> removeFuture(int sequence);

}
