package com.dinstone.photon;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import io.netty.handler.ssl.ClientAuth;

public class AcceptOptions extends TcpSslOptions {

    /**
     * The default accept event loop size = 1
     */
    public static final int DEFAULT_ACCEPT_SIZE = 1;

    /**
     * The default worker event loop size = 0 (2 * Runtime.availableProcessors).
     */
    public static final int DEFAULT_WORKER_SIZE = 0;

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

    /**
     * Default value of processor thread size = 20
     */
    private static final int DEFAULT_PROCESSOR_SIZE = 20;

    private int acceptSize;
    private int workerSize;
    private int processorSize;

    private int acceptBacklog;
    private ClientAuth clientAuth;
    private PrivateKey privateKey;
    private X509Certificate[] certChain;

    public AcceptOptions() {
        super();
        acceptSize = DEFAULT_ACCEPT_SIZE;
        workerSize = DEFAULT_WORKER_SIZE;
        processorSize = DEFAULT_PROCESSOR_SIZE;
        acceptBacklog = DEFAULT_ACCEPT_BACKLOG;
        clientAuth = DEFAULT_CLIENT_AUTH;
    }

    public void setProcessorSize(int processorSize) {
        this.processorSize = processorSize;
    }

    public int getProcessorSize() {
        return processorSize;
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

}
