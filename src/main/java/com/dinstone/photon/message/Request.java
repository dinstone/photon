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

public class Request extends Message {

    private final long arrival;

    public Request() {
        super(Message.DEFAULT_VERSION, Message.Type.REQUEST);
        arrival = System.currentTimeMillis();
    }

    public int getTimeout() {
        return getFlag();
    }

    public void setTimeout(int timeout) {
        setFlag((short) timeout);
    }

    public boolean isTimeout() {
        long timeout = getTimeout();
        return (timeout > 0 && arrival - System.currentTimeMillis() >= timeout);
    }

    @Override
    public String toString() {
        return "Request [timeout=" + getTimeout() + ", msgId=" + msgId + ", headers=" + headers + "]";
    }

}
