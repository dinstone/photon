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

public class Request extends BurdenMessage {

    private int timeout;

    public Request() {
        super(Message.REQUEST);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void encode(ByteBuf oBuffer) throws Exception {
        super.encode(oBuffer);

        oBuffer.writeByte(codec);
        oBuffer.writeInt(timeout);

        // headers
        writeData(oBuffer, Headers.encode(headers));
        // content
        writeData(oBuffer, content);
    }

    @Override
    public void decode(ByteBuf iBuffer) throws Exception {
        super.decode(iBuffer);

        codec = iBuffer.readByte();
        // timout
        timeout = iBuffer.readInt();
        // headers
        headers = Headers.decode(readData(iBuffer));
        // content
        content = readData(iBuffer);
    }

}
