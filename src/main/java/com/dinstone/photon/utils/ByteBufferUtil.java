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
package com.dinstone.photon.utils;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class ByteBufferUtil {

    private static final String EMPTY = "";

    public static String readString(ByteBuf bb) {
        int length = bb.readInt();
        if (length <= 0) {
            return EMPTY;
        } else {
            byte[] bytes = new byte[length];
            bb.readBytes(bytes);
            return new String(bytes, CharsetUtil.UTF_8);
        }
    }

    public static void writeString(ByteBuf bb, String str) {
        if (str == null || str.isEmpty()) {
            bb.writeInt(0);
        } else {
            byte[] strBytes = str.getBytes(CharsetUtil.UTF_8);
            bb.writeInt(strBytes.length);
            bb.writeBytes(strBytes);
        }
    }

}
