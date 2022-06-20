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
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.message.Heartbeat;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

public class DefaultMessageProcessor implements MessageProcessor {

    @Override
    public void process(Connection connection, Notice msg) {
    }

    @Override
    public void process(Connection connection, Request msg) {
    }

    @Override
    public void process(Connection connection, Response msg) {
    }

    @Override
    public void process(Connection connection, Heartbeat msg) {
    }

}