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
package com.dinstone.photon;

import javax.net.ssl.TrustManagerFactory;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class ConnectOptions extends TransportOptions {

    /**
     * The default value of connect timeout = 3000 ms
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;

    /**
     * The default value of eventloop size = 1
     */
    public static final int DEFAULT_EVENTLOOP_SIZE = 1;

    private int eventLoopSize;
    private int connectTimeout;
    private String localAddress;
    private TrustManagerFactory trustManagerFactory;

    public ConnectOptions() {
        super();

        eventLoopSize = DEFAULT_EVENTLOOP_SIZE;
        connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        trustManagerFactory = InsecureTrustManagerFactory.INSTANCE;
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

    public int getEventLoopSize() {
        return eventLoopSize;
    }

    public void setEventLoopSize(int eventLoopSize) {
        this.eventLoopSize = eventLoopSize;
    }

    public TrustManagerFactory getTrustManagerFactory() {
        return trustManagerFactory;
    }

    public void setTrustManagerFactory(TrustManagerFactory trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
    }

}
