/*
 * Copyright (C) 2018~2022 dinstone<dinstone@163.com>
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

    private static final String status_name = ":status";

    public Response() {
        super(Message.DEFAULT_VERSION, Message.Type.RESPONSE);
    }

    public Status getStatus() {
        return Status.valueOf(headers().getInt(status_name));
    }

    public void setStatus(Status status) {
        headers().setInt(status_name, status.value);
    }

    public enum Status {
        SUCCESS(0), // message handle success
        FAILURE(1), // message handle failure
        TIMEOUT(2); // message handle timeout

        private int value;

        private Status(int value) {
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
            case 2:
                return TIMEOUT;
            default:
                break;
            }
            throw new IllegalArgumentException("unsupported status type [" + value + "]");
        }

    }
}
