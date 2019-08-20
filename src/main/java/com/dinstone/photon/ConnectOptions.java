package com.dinstone.photon;

public class ConnectOptions extends TcpSslOptions {

    /**
     * The default value of connect timeout = 60000 ms
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 60000;

    /**
     * The default value of whether all servers (SSL/TLS) should be trusted = false
     */
    public static final boolean DEFAULT_TRUST_ALL = false;

    private int connectTimeout;
    private boolean trustAll;
    private String localAddress;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isTrustAll() {
        return trustAll;
    }

    public void setTrustAll(boolean trustAll) {
        this.trustAll = trustAll;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getEventLoopSize() {
        // TODO Auto-generated method stub
        return 0;
    }

}
