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

public class Notice extends Message {

    private static final String ADDRESS_KEY = "notice.topic";

    public Notice() {
        super(Message.DEFAULT_VERSION, Message.Type.NOTICE);
    }

    @Deprecated
    public String getAddress() {
        return getTopic();
    }

    @Deprecated
    public void setAddress(String address) {
        setTopic(address);
    }

    public String getTopic() {
        return headers().get(ADDRESS_KEY);
    }

    public void setTopic(String topic) {
        headers().set(ADDRESS_KEY, topic);
    }

    @Override
    public String toString() {
        return "Notice [sequence=" + sequence + ", headers=" + headers + "]";
    }

}
