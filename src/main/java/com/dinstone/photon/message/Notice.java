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

public class Notice extends LoadedMessage {

    private String address;

    public Notice() {
        super(Message.NOTICE);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void encode(ByteBuf oBuffer) throws Exception {
        super.encode(oBuffer);

        oBuffer.writeByte(codec);
        writeString(oBuffer, address);

        // headers
        writeData(oBuffer, Headers.encode(headers));
        // content
        writeData(oBuffer, content);
    }

    @Override
    public void decode(ByteBuf iBuffer) throws Exception {
        super.decode(iBuffer);

        codec = iBuffer.readByte();
        // address
        address = readString(iBuffer);
        // headers
        headers = Headers.decode(readData(iBuffer));
        // content
        content = readData(iBuffer);
    }

}
