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
package com.dinstone.photon.endpoint.client;

import java.net.InetSocketAddress;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.ConnectOptions;
import com.dinstone.photon.Connection;
import com.dinstone.photon.Connector;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

public class ConnectorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectorTest.class);

    public static void main(String[] args) throws Throwable {
        ConnectOptions connectOptions = new ConnectOptions();
        // connectOptions.setEnableSsl(true);
        Connector connector = new Connector(connectOptions);

        Connection connection = connector.connect(new InetSocketAddress("127.0.0.1", 4444));
        LOG.info("channel active is {}", connection.isActive());

        Request request = new Request();
        request.setSequence(1);
        request.setTimeout(10000);
        request.setContent("Hello World".getBytes());

        LOG.info("async request is  {}", request);
        connection.sendRequest(request).thenAccept(response -> {
            LOG.info("async response is {}", response);
        });

        try {
            for (int i = 2; i < 50000; i++) {
                request = new Request();
                request.setSequence(i);
                request.setTimeout(3000);

                LOG.info("sync request is  {}", request);
                Response response = connection.sendRequest(request).get();
                LOG.info("sync response is {}", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("error : ", e);
        }

        // System.in.read();

        connector.destroy().awaitUninterruptibly();
    }

}
