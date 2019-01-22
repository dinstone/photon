/*
 * Copyright (C) 2013~2017 dinstone<dinstone@163.com>
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

package com.dinstone.photon.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.dinstone.photon.crypto.Cipher;

import io.netty.buffer.ByteBuf;

/**
 * transport frame
 * 
 * @author dinstone
 */
public class Frame {

    private static final byte WIRE_VERSION = 1;

    protected boolean zipped;

    protected byte codec;

    protected byte[] datas;

    public Frame() {
        super();
    }

    public Frame(byte codec, byte[] datas) {
        super();
        this.codec = codec;
        this.datas = datas;
    }

    public Frame enzip() throws Exception {
        if (!zipped && datas != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(datas);
            gzip.close();
            datas = out.toByteArray();
            zipped = true;
        }
        return this;
    }

    public Frame dezip() throws Exception {
        if (zipped && datas != null) {
            int len;
            byte[] buffer = new byte[8192];
            GZIPInputStream ungzip = new GZIPInputStream(new ByteArrayInputStream(datas));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = ungzip.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            datas = out.toByteArray();
            zipped = false;
        }
        return this;
    }

    public Frame encrypt(Cipher cipher) throws Exception {
        if (cipher != null && datas != null) {
            datas = cipher.encrypt(datas);
        }
        return this;
    }

    public Frame decrypt(Cipher cipher) throws Exception {
        if (cipher != null && datas != null) {
            datas = cipher.decrypt(datas);
        }
        return this;
    }

    public Frame encode(ByteBuf out) {
        out.writeInt(3 + datas.length);
        out.writeByte(WIRE_VERSION);
        out.writeBoolean(zipped);
        // out.writeBoolean(crypto);
        out.writeByte(codec);
        out.writeBytes(datas);

        return this;
    }

    public Frame decode(ByteBuf in) {
        int len = in.readInt();
        byte version = in.readByte();
        if (WIRE_VERSION != version) {
            throw new IllegalStateException("Invalid wire version " + version + " should be <= " + WIRE_VERSION);
        }
        zipped = in.readBoolean();
        // crypto = in.readBoolean();
        codec = in.readByte();

        datas = new byte[len - 3];
        in.readBytes(datas);

        return this;
    }

    public boolean isZipped() {
        return zipped;
    }

    public byte getCodec() {
        return codec;
    }

    public byte[] getDatas() {
        return datas;
    }

}