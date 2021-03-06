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
package com.dinstone.photon.transport.server;

import java.net.InetSocketAddress;
import java.security.cert.X509Certificate;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AcceptOptions;
import com.dinstone.photon.Acceptor;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Response.Status;
import com.dinstone.photon.processor.MessageProcessor;
import com.dinstone.photon.processor.ProcessContext;

import io.netty.handler.ssl.util.SelfSignedCertificate;

public class AcceptorTest {
    private static final Logger LOG = LoggerFactory.getLogger(AcceptorTest.class);

    public static void main(String[] args) throws Exception {
        AcceptOptions acceptOptions = new AcceptOptions();
        acceptOptions.setEnableSsl(true);
        SelfSignedCertificate cert = new SelfSignedCertificate();
        acceptOptions.setPrivateKey(cert.key());
        acceptOptions.setCertChain(new X509Certificate[] { cert.cert() });
        Acceptor acceptor = new Acceptor(acceptOptions);
        acceptor.setMessageProcessor(new MessageProcessor() {

            @Override
            public void process(ProcessContext ctx, Object msg) {
                Request req = (Request) msg;
                LOG.info("Request is {},{}", req.getMsgId(), req.getCodec());
                Notice notice = new Notice();
                notice.setAddress("");
                notice.setContent(req.getContent());
                ctx.send(notice);

                Response response = new Response();
                response.setMsgId(req.getMsgId());
                response.setStatus(Status.SUCCESS);
                response.setContent(req.getContent());
                ctx.send(response);
            }
        });

        acceptor.bind(new InetSocketAddress("127.0.0.1", 4444));

        System.in.read();

        acceptor.destroy();
    }

}
