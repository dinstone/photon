package com.dinstone.photon;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

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
    private PrivateKey privateKey;
    private X509Certificate[] certChain;

    public AcceptOptions() {
        super();
        port = DEFAULT_PORT;
        host = DEFAULT_HOST;
        acceptBacklog = DEFAULT_ACCEPT_BACKLOG;
        clientAuth = DEFAULT_CLIENT_AUTH;
    }

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

}
