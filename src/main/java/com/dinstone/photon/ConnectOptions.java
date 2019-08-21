package com.dinstone.photon;

import javax.net.ssl.TrustManagerFactory;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class ConnectOptions extends TcpSslOptions {

    /**
     * The default value of connect timeout = 3000 ms
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;

    /**
     * The default value of eventloop size = 0
     */
    public static final int DEFAULT_EVENTLOOP_SIZE = 0;

    private int connectTimeout;
    private int eventLoopSize;
    private String localAddress;
    private TrustManagerFactory trustManagerFactory;

    public ConnectOptions() {
        super();

        connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        eventLoopSize = DEFAULT_EVENTLOOP_SIZE;
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
