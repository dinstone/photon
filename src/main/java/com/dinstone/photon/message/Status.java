/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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
package com.dinstone.photon.message;

/**
 * response status
 *
 * @author guojinfei
 * @version 1.0.0.2014-6-23
 */
public enum Status {
    SUCCESS((byte) 0), //
    UNKNOWN((byte) 1), //
    TIMEOUT((byte) 2), //
    ERROR((byte) 3); //

    private byte value;

    private Status(byte value) {
        this.value = value;
    }

    /**
     * the value to get
     *
     * @return the value
     * @see Status#value
     */
    public byte getValue() {
        return value;
    }

    public static Status valueOf(byte value) {
        switch (value) {
        case 0:
            return SUCCESS;
        case 1:
            return UNKNOWN;
        case 2:
            return TIMEOUT;
        case 3:
            return ERROR;

        default:
            break;
        }
        throw new IllegalArgumentException("unsupported message type [" + value + "]");
    }

}
