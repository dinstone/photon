package com.dinstone.photon;

import io.netty.handler.ssl.ClientAuth;

public class AcceptOptions extends TcpSslOptions {

    /**
     * The default port to listen on = 0 (meaning a random ephemeral free port will
     * be chosen)
     */
    public static final int DEFAULT_PORT = 0;

    /**
     * The default host to listen on = "0.0.0.0" (meaning listen on all available
     * interfaces).
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

    private int port;
    private String host;
    private int acceptBacklog;
    private ClientAuth clientAuth;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

}
