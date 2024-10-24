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

import javax.net.ssl.TrustManagerFactory;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class ConnectOptions extends TransportOptions {

    /**
     * The default value of connect timeout = 1000 ms
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 1000;

    /**
     * The default worker event loop size = 1.
     */
    public static final int DEFAULT_WORKER_SIZE = 1;

    private int workerSize;
    private int connectTimeout;
    private String localAddress;
    private TrustManagerFactory trustManagerFactory;

    public ConnectOptions() {
        super();

        workerSize = DEFAULT_WORKER_SIZE;
        connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        trustManagerFactory = InsecureTrustManagerFactory.INSTANCE;
    }

    public ConnectOptions(ConnectOptions other) {
        super(other);

        workerSize = other.workerSize;
        localAddress = other.localAddress;
        connectTimeout = other.connectTimeout;
        trustManagerFactory = other.trustManagerFactory;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getWorkerSize() {
        return workerSize;
    }

    public void setWorkerSize(int workerSize) {
        this.workerSize = workerSize;
    }

    public TrustManagerFactory getTrustManagerFactory() {
        return trustManagerFactory;
    }

    public void setTrustManagerFactory(TrustManagerFactory trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
    }

}
