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
package com.dinstone.photon.endpoint;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AcceptOptions;
import com.dinstone.photon.Acceptor;
import com.dinstone.photon.ConnectOptions;
import com.dinstone.photon.Connection;
import com.dinstone.photon.Connector;
import com.dinstone.photon.Processor;
import com.dinstone.photon.endpoint.client.ConnectorTest;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

public class TransportTest {

    private static final Logger LOG = LoggerFactory.getLogger(TransportTest.class);

    public static void main(String[] args) throws Exception {
        Acceptor acceptor = getAcceptor();

        ConnectOptions connectOptions = new ConnectOptions();
        Connector connector = new Connector(connectOptions);

        Connection connection = connector.connect(new InetSocketAddress("127.0.0.1", 5555));
        LOG.info("channel active is {}", connection.isActive());

        Request request = new Request();
        request.setSequence(1);
        request.setTimeout(10000);
        request.setContent("Hello World".getBytes());

        LOG.info("async request is  {}", request);
        connection.sendRequest(request).thenAccept(response -> {
            LOG.info("async response is {}", response);
        });

        request = new Request();
        request.setSequence(2);
        request.setTimeout(3000);

        LOG.info("sync request is  {}", request);
        Response response = connection.sendRequest(request).get();
        LOG.info("sync response is {}", response);

        System.in.read();

        connector.destroy().awaitUninterruptibly();
        acceptor.destroy().awaitUninterruptibly();
    }

    private static Acceptor getAcceptor() throws Exception {
        AcceptOptions acceptOptions = new AcceptOptions();
        Acceptor acceptor = new Acceptor(acceptOptions);
        acceptor.setProcessor(new Processor() {

            @Override
            public void process(Connection connection, Request req) {
                LOG.info("Request is {}", req.getSequence());
                Notice notice = new Notice();
                notice.setTopic("order.created");
                notice.setContent(req.getContent());
                CompletableFuture<Void> f = connection.sendNotice(notice);
                f.thenAccept((v) -> {
                    Response response = new Response();
                    response.setSequence(req.getSequence());
                    response.setStatus(Response.Status.SUCCESS);
                    response.setContent(req.getContent());
                    connection.sendResponse(response);
                });
            }

        });

        acceptor.bind(new InetSocketAddress("127.0.0.1", 5555));
        return acceptor;
    }
}
