/*
 * Copyright (C) 2018~2020 dinstone<dinstone@163.com>
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

public abstract class NetworkOptions {

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

    private boolean tcpNoDelay;
    private boolean tcpKeepAlive;
    private int soLinger;

    /**
     * Default constructor
     */
    public NetworkOptions() {
        sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
        receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
        reuseAddress = DEFAULT_REUSE_ADDRESS;
        trafficClass = DEFAULT_TRAFFIC_CLASS;
        logActivity = DEFAULT_LOG_ENABLED;

        tcpNoDelay = DEFAULT_TCP_NO_DELAY;
        tcpKeepAlive = DEFAULT_TCP_KEEP_ALIVE;
        soLinger = DEFAULT_SO_LINGER;
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

}
