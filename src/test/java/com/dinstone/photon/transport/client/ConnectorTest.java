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
package com.dinstone.photon.transport.client;

import java.net.InetSocketAddress;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.ConnectOptions;
import com.dinstone.photon.Connector;
import com.dinstone.photon.connection.Connection;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.processor.MessageProcessor;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ConnectorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectorTest.class);

    public static void main(String[] args) throws Throwable {
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setEnableSsl(true);
        Connector connector = new Connector(connectOptions);
        connector.setMessageProcessor(new MessageProcessor() {

            @Override
            public void process(Connection connection, Object msg) {
                LOG.info("Request is {}", msg);
            }

        });

        Connection connection = connector.connect(new InetSocketAddress("127.0.0.1", 4444));
        LOG.info("channel active is {}", connection.isActive());

        Request request = new Request();
        request.setMsgId(1);
        request.setCodec((byte) 1);
        request.setTimeout(10000);
        request.setContent("Hello World".getBytes());

        Response response = connection.sync(request);
        LOG.info("sync response is {}", response.getContent());

        connection.async(request).addListener(new GenericFutureListener<Future<Response>>() {

            @Override
            public void operationComplete(Future<Response> future) throws Exception {
                try {
                    LOG.info("thread {}", Thread.currentThread().getName());
                    Response response = future.get();
                    LOG.info("async response is {}", response.getContent());
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        });

        System.in.read();

        connector.destroy();
    }

}
