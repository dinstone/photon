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

public class Notice extends Message {

    private static final String topic_name = "notice.topic";

    public Notice() {
        super(Message.DEFAULT_VERSION, Message.Type.NOTICE);
    }

    public String getAddress() {
        return headers().get(topic_name);
    }

    public void setAddress(String address) {
        headers().set(topic_name, address);
    }

    @Override
    public String toString() {
        return "Notice [msgId=" + msgId + ", headers=" + headers + "]";
    }

}
