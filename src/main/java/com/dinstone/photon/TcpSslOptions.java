package com.dinstone.photon;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class TcpSslOptions extends NetworkOptions {

    /**
     * The default value of TCP-no-delay = true (Nagle disabled)
     */
    public static final boolean DEFAULT_TCP_NO_DELAY = true;

    /**
     * The default value of TCP keep alive = false
     */
    public static final boolean DEFAULT_TCP_KEEP_ALIVE = false;

    /**
     * The default value of SO_linger = -1
     */
    public static final int DEFAULT_SO_LINGER = -1;

    /**
     * The default value of Netty use pooled buffers = false
     */
    public static final boolean DEFAULT_USE_POOLED_BUFFERS = false;

    /**
     * SSL enable by default = false
     */
    public static final boolean DEFAULT_SSL = false;

    /**
     * Default idle timeout = 30
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 30;

    /**
     * Default use alpn = false
     */
    public static final boolean DEFAULT_USE_ALPN = false;

    private boolean tcpNoDelay;
    private boolean tcpKeepAlive;
    private int soLinger;
    private boolean usePooledBuffers;
    private int idleTimeout;
    private boolean enableSsl;
//    private KeyCertOptions keyCertOptions;
//    private TrustOptions trustOptions;
    private Set<String> enabledCipherSuites = new LinkedHashSet<>();
    private ArrayList<String> crlPaths;
    private ArrayList<Buffer> crlValues;
//    private SslEngineOptions sslEngineOptions;
    private Set<String> enabledSecureTransportProtocols = new LinkedHashSet<>();

    public TcpSslOptions() {
        super();

        tcpNoDelay = DEFAULT_TCP_NO_DELAY;
        tcpKeepAlive = DEFAULT_TCP_KEEP_ALIVE;
        soLinger = DEFAULT_SO_LINGER;

        usePooledBuffers = DEFAULT_USE_POOLED_BUFFERS;
        idleTimeout = DEFAULT_IDLE_TIMEOUT;
        enableSsl = DEFAULT_SSL;
        crlPaths = new ArrayList<>();
        crlValues = new ArrayList<>();
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isTcpKeepAlive() {
        return tcpKeepAlive;
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        this.tcpKeepAlive = tcpKeepAlive;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public boolean isUsePooledBuffers() {
        return usePooledBuffers;
    }

    public void setUsePooledBuffers(boolean usePooledBuffers) {
        this.usePooledBuffers = usePooledBuffers;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public Set<String> getEnabledCipherSuites() {
        return enabledCipherSuites;
    }

    public void setEnabledCipherSuites(Set<String> enabledCipherSuites) {
        this.enabledCipherSuites = enabledCipherSuites;
    }

    public ArrayList<String> getCrlPaths() {
        return crlPaths;
    }

    public void setCrlPaths(ArrayList<String> crlPaths) {
        this.crlPaths = crlPaths;
    }

    public ArrayList<Buffer> getCrlValues() {
        return crlValues;
    }

    public void setCrlValues(ArrayList<Buffer> crlValues) {
        this.crlValues = crlValues;
    }

    public Set<String> getEnabledSecureTransportProtocols() {
        return enabledSecureTransportProtocols;
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        this.enabledSecureTransportProtocols = enabledSecureTransportProtocols;
    }

}
