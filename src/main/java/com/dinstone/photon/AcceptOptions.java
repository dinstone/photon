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
package com.dinstone.photon;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import io.netty.handler.ssl.ClientAuth;

public class AcceptOptions extends PhotonOptions {

    /**
     * The default accept event loop size = 1
     */
    public static final int DEFAULT_ACCEPT_SIZE = 1;

    /**
     * The default worker event loop size = 0 (2 * Runtime.availableProcessors).
     */
    public static final int DEFAULT_WORKER_SIZE = 0;

    /**
     * The default host to listen on = "0.0.0.0" (meaning listen on all available interfaces).
     */
    public static final String DEFAULT_HOST = "0.0.0.0";

    /**
     * The default accept backlog = 1024
     */
    public static final int DEFAULT_ACCEPT_BACKLOG = -1;

    /**
     * Default value of whether client auth is required (SSL/TLS) = No
     */
    public static final ClientAuth DEFAULT_CLIENT_AUTH = ClientAuth.NONE;

    /**
     * zero is no limit
     */
    private static final int DEFAULT_CONNECTION_LIMIT = 0;

    private int acceptSize;
    private int workerSize;
    private int acceptBacklog;
    private ClientAuth clientAuth;
    private PrivateKey privateKey;
    private X509Certificate[] certChain;

    private int connectionLimit;

    public AcceptOptions() {
        super();
        acceptSize = DEFAULT_ACCEPT_SIZE;
        workerSize = DEFAULT_WORKER_SIZE;

        acceptBacklog = DEFAULT_ACCEPT_BACKLOG;
        clientAuth = DEFAULT_CLIENT_AUTH;

        connectionLimit = DEFAULT_CONNECTION_LIMIT;
    }

    public int getAcceptSize() {
        return acceptSize;
    }

    public void setAcceptSize(int acceptSize) {
        this.acceptSize = acceptSize;
    }

    public int getWorkerSize() {
        return workerSize;
    }

    public void setWorkerSize(int workerSize) {
        this.workerSize = workerSize;
    }

    public int getAcceptBacklog() {
        return acceptBacklog;
    }

    public void setAcceptBacklog(int acceptBacklog) {
        this.acceptBacklog = acceptBacklog;
    }

    public ClientAuth getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(ClientAuth clientAuth) {
        this.clientAuth = clientAuth;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public X509Certificate[] getCertChain() {
        return certChain;
    }

    public void setCertChain(X509Certificate[] certChain) {
        this.certChain = certChain;
    }

    public int getConnectionLimit() {
        return connectionLimit;
    }

    public void setConnectionLimit(int connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

}
