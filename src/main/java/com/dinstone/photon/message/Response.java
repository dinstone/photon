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
package com.dinstone.photon.message;

public class Response extends Message {

    public Response() {
        super(Message.DEFAULT_VERSION, Message.Type.RESPONSE);
    }

    public Status getStatus() {
        return Status.valueOf(getFlag());
    }

    public void setStatus(Status status) {
        setFlag((short) status.value);
    }

    @Override
    public String toString() {
        return "Response [status=" + getStatus() + ", sequence=" + sequence + ", headers=" + headers + "]";
    }

    public enum Status {
        SUCCESS(0), // message handle success
        FAILURE(1); // message handle failure

        private final int value;

        Status(int value) {
            this.value = value;
        }

        /**
         * the value to get
         *
         * @return the value
         * 
         * @see Status#value
         */
        public int getValue() {
            return value;
        }

        public static Status valueOf(int value) {
            switch (value) {
            case 0:
                return SUCCESS;
            case 1:
                return FAILURE;
            default:
                break;
            }
            throw new IllegalArgumentException("unsupported status type [" + value + "]");
        }

    }
}
