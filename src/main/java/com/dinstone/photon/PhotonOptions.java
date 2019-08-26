package com.dinstone.photon;

public abstract class PhotonOptions extends NetworkOptions {

    /**
     * The default value of Netty use pooled buffers = false
     */
    public static final boolean DEFAULT_USE_POOLED_BUFFERS = true;

    /**
     * SSL enable by default = false
     */
    public static final boolean DEFAULT_ENABLE_SSL = false;

    /**
     * Default idle timeout = 30s
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 30;

    /**
     * Default processor thread size = 20
     */
    private static final int DEFAULT_PROCESSOR_SIZE = 20;

    private boolean usePooledBuffers;
    private boolean enableSsl;
    private int idleTimeout;

    private int processorSize;

    public PhotonOptions() {
        super();

        usePooledBuffers = DEFAULT_USE_POOLED_BUFFERS;
        idleTimeout = DEFAULT_IDLE_TIMEOUT;
        enableSsl = DEFAULT_ENABLE_SSL;

        processorSize = DEFAULT_PROCESSOR_SIZE;
    }

    public int getProcessorSize() {
        return processorSize;
    }

    public void setProcessorSize(int processorSize) {
        this.processorSize = processorSize;
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

}
