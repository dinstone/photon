/*
 * Copyright (C) 2018~2021 dinstone<dinstone@163.com>
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

import io.netty.buffer.ByteBuf;

public class Heartbeat extends ControlMessage {

    /**
     * true:ping / false:pong
     */
    private boolean tick;

    public Heartbeat() {
        super(Message.HEARTBEAT);
    }

    public Heartbeat(int messageId, boolean tick) {
        super(Message.HEARTBEAT);
        this.msgId = messageId;
        this.tick = tick;
    }

    public Heartbeat ping() {
        this.tick = true;
        return this;
    }

    public Heartbeat pong() {
        this.tick = false;
        return this;
    }

    public boolean getTick() {
        return tick;
    }

    public boolean isPing() {
        return tick;
    }

    public boolean isPong() {
        return !tick;
    }

    @Override
    public String toString() {
        return "Heartbeat[id=" + getMsgId() + ",tick=" + tick + "]";
    }

    @Override
    public void encode(ByteBuf oBuffer) throws Exception {
        super.encode(oBuffer);

        oBuffer.writeBoolean(tick);
    }

    @Override
    public void decode(ByteBuf iBuffer) throws Exception {
        super.decode(iBuffer);

        tick = iBuffer.readBoolean();
    }

}
