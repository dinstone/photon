package com.dinstone.photon;

public abstract class NetworkOptions {

    /**
     * The default value of TCP send buffer size
     */
    public static final int DEFAULT_SEND_BUFFER_SIZE = -1;

    /**
     * The default value of TCP receive buffer size
     */
    public static final int DEFAULT_RECEIVE_BUFFER_SIZE = -1;

    /**
     * The default value of traffic class
     */
    public static final int DEFAULT_TRAFFIC_CLASS = -1;

    /**
     * The default value of reuse address
     */
    public static final boolean DEFAULT_REUSE_ADDRESS = true;

    /**
     * The default log enabled = false
     */
    public static final boolean DEFAULT_LOG_ENABLED = false;

    private int sendBufferSize;
    private int receiveBufferSize;
    private int trafficClass;
    private boolean reuseAddress;
    private boolean logActivity;

    /**
     * Default constructor
     */
    public NetworkOptions() {
        sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
        receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
        reuseAddress = DEFAULT_REUSE_ADDRESS;
        trafficClass = DEFAULT_TRAFFIC_CLASS;
        logActivity = DEFAULT_LOG_ENABLED;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getTrafficClass() {
        return trafficClass;
    }

    public void setTrafficClass(int trafficClass) {
        this.trafficClass = trafficClass;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public boolean isLogActivity() {
        return logActivity;
    }

    public void setLogActivity(boolean logActivity) {
        this.logActivity = logActivity;
    }

}