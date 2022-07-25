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

public class Heartbeat extends Message {

    public Heartbeat() {
        super(Message.DEFAULT_VERSION, Message.Type.HEARTBEAT);
    }

    public Heartbeat ping() {
        setFlag((short) 1);
        return this;
    }

    public boolean isPing() {
        return getFlag() == 1;
    }

    public Heartbeat pong() {
        setFlag((short) 0);
        return this;
    }

    public boolean isPong() {
        return !isPing();
    }

    @Override
    public String toString() {
        return "Heartbeat [msgId=" + msgId + ", tick=" + isPing() + ", headers=" + headers + "]";
    }

}
