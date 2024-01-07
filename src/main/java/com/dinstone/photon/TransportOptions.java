/*
 * Copyright (C) 2018~2024 dinstone<dinstone@163.com>
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

public abstract class TransportOptions extends NetworkOptions {

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

    private boolean usePooledBuffers;
    private boolean enableSsl;
    private int idleTimeout;

    public TransportOptions() {
        super();

        usePooledBuffers = DEFAULT_USE_POOLED_BUFFERS;
        idleTimeout = DEFAULT_IDLE_TIMEOUT;
        enableSsl = DEFAULT_ENABLE_SSL;
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
