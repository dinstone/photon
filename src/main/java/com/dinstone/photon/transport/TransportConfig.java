/*
 * Copyright (C) 2013~2017 dinstone<dinstone@163.com>
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

package com.dinstone.photon.transport;

import java.util.Properties;

import com.dinstone.photon.Configuration;

public class TransportConfig extends Configuration {

    /** max size */
    private static final String MAX_SIZE = "rpc.max.size";

    /** Connect Timeout */
    private static final String CONNECT_TIMEOUT = "rpc.connect.timeout";

    private static final String HEARTBEAT_INTERVAL_SECONDS = "heartbeat.interval.seconds";

    private static final String CONNECT_POOL_SIZE = "connect.pool.size";

    /** parallel count */
    private static final String BUSINESS_PROCESSOR_COUNT = "business.processor.count";

    private static final String NIO_PROCESSOR_COUNT = "nio.processor.count";

    private static final String MAX_CONNECTION_COUNT = "max.connection.count";

    private static final String ENABLE_CRYPT = "enable.crypt";

    public TransportConfig() {
    }

    public TransportConfig(Configuration config) {
        super(config);
    }

    public TransportConfig(String configLocation) {
        super(configLocation);
    }

    public int getMaxSize() {
        return getInt(MAX_SIZE, Integer.MAX_VALUE);
    }

    public TransportConfig setMaxSize(int maxSize) {
        setInt(MAX_SIZE, maxSize);

        return this;
    }

    public int getConnectTimeout() {
        return getInt(CONNECT_TIMEOUT, 3000);
    }

    public TransportConfig setConnectTimeout(int timeout) {
        setInt(CONNECT_TIMEOUT, timeout);

        return this;
    }

    public int getHeartbeatIntervalSeconds() {
        return getInt(HEARTBEAT_INTERVAL_SECONDS, 60);
    }

    public TransportConfig setHeartbeatIntervalSeconds(int interval) {
        setInt(HEARTBEAT_INTERVAL_SECONDS, interval);

        return this;
    }

    public int getConnectPoolSize() {
        return getInt(CONNECT_POOL_SIZE, 2);
    }

    public TransportConfig setConnectPoolSize(int size) {
        if (size > 0) {
            setInt(CONNECT_POOL_SIZE, size);
        }
        return this;
    }

    public int getNioProcessorCount() {
        return getInt(NIO_PROCESSOR_COUNT, Runtime.getRuntime().availableProcessors());
    }

    public TransportConfig setNioProcessorCount(int count) {
        if (count > 0) {
            setInt(NIO_PROCESSOR_COUNT, count);
        }
        return this;
    }

    public int getBusinessProcessorCount() {
        return getInt(BUSINESS_PROCESSOR_COUNT, 0);
    }

    public TransportConfig setBusinessProcessorCount(int count) {
        setInt(BUSINESS_PROCESSOR_COUNT, count);

        return this;
    }

    public int getMaxConnectionCount() {
        return getInt(MAX_CONNECTION_COUNT, 1000);
    }

    public TransportConfig setMaxConnectionCount(int count) {
        if (count > 0) {
            setInt(MAX_CONNECTION_COUNT, count);
        }
        return this;
    }

    @Override
    public TransportConfig setProperties(Properties other) {
        super.setProperties(other);

        return this;
    }

    public boolean enableCrypt() {
        return getBoolean(ENABLE_CRYPT, true);
    }

}
