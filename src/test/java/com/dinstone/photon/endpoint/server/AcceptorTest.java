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
package com.dinstone.photon.endpoint.server;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AcceptOptions;
import com.dinstone.photon.Acceptor;
import com.dinstone.photon.Connection;
import com.dinstone.photon.Processor;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;

public class AcceptorTest {
    private static final Logger LOG = LoggerFactory.getLogger(AcceptorTest.class);

    public static void main(String[] args) throws Exception {
        AcceptOptions acceptOptions = new AcceptOptions();
        // acceptOptions.setEnableSsl(false);
        // acceptOptions.setIdleTimeout(60000);
        // SelfSignedCertificate cert = new SelfSignedCertificate();
        // acceptOptions.setPrivateKey(cert.key());
        // acceptOptions.setCertChain(new X509Certificate[] { cert.cert() });
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
                    response.setContent(req.getContent());
                    connection.sendResponse(response);
                });
            }

        });

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 4444);
        acceptor.bind(address);

        LOG.info("listen on: {}", address);

        System.in.read();

        acceptor.destroy().awaitUninterruptibly();
    }

}
