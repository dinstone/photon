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

import java.io.IOException;

public abstract class LoadedMessage extends AbstractMessage {

    protected byte codec;

    protected Headers headers;

    protected byte[] content;

    public LoadedMessage(byte type) {
        super(type);
    }

    public byte getCodec() {
        return codec;
    }

    public void setCodec(byte codec) {
        this.codec = codec;
    }

    public Headers headers() {
        if (headers == null) {
            headers = new Headers();
        }
        return headers;
    }

    public byte[] getHeaders() {
        try {
            return Headers.encode(headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHeaders(byte[] headers) {
        try {
            if (headers != null) {
                Headers hs = Headers.decode(headers);
                if (hs != null) {
                    this.headers = hs;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

}
