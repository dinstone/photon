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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map.Entry;

import com.dinstone.photon.utils.ByteBufferUtil;
import com.dinstone.photon.utils.ByteStreamUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.internal.PlatformDependent;

/**
 * 
 * <pre>
 *    Header Size (16)     
 * |----------|----------|
 *    Key Length (16)     
 * |----------|----------|
 *   Key Content(UTF-8)
 * |~~~~~~~~~~~~~~~~~~~~~|
 *    Value Length (16) 
 * |----------|----------|
 *   Value Content(UTF-8)
 * |~~~~~~~~~~~~~~~~~~~~~|
 * </pre>
 * 
 * @author dinstone
 *
 */
public class Headers extends DefaultHeaders<String, String, Headers> {

    private static final StringValueConverter VALUE_CONVERTER = new StringValueConverter();

    public Headers() {
        super(VALUE_CONVERTER);
    }

    byte[] encode() throws IOException {
        if (this.isEmpty()) {
            return null;
        } else {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            // count
            ByteStreamUtil.writeShort(bao, this.size());
            for (Entry<String, String> element : this) {
                ByteStreamUtil.writeString(bao, element.getKey());
                ByteStreamUtil.writeString(bao, element.getValue());
            }
            return bao.toByteArray();
        }
    }

    void decode(byte[] hsBytes) throws IOException {
        ByteArrayInputStream bai = new ByteArrayInputStream(hsBytes);
        int count = ByteStreamUtil.readShort(bai);
        for (int i = 0; i < count; i++) {
            String k = ByteStreamUtil.readString(bai);
            String v = ByteStreamUtil.readString(bai);
            this.add(k, v);
        }
    }

    Headers decode(ByteBuf bb) throws IOException {
        int count = bb.readShort();
        for (int i = 0; i < count; i++) {
            String k = ByteBufferUtil.readString(bb);
            String v = ByteBufferUtil.readString(bb);
            this.add(k, v);
        }
        return this;
    }

    Headers encode(ByteBuf bb) throws IOException {
        if (this.isEmpty()) {
            bb.writeShort(0);
        } else {
            // count
            bb.writeShort(this.size());
            for (Entry<String, String> element : this) {
                ByteBufferUtil.writeString(bb, element.getKey());
                ByteBufferUtil.writeString(bb, element.getValue());
            }
        }
        return this;
    }

    public Headers setAll(Iterable<Entry<String, String>> headers) {
        if (headers != null && headers != this) {
            headers.forEach(e -> {
                if (e.getValue() == null) {
                    remove(e.getKey());
                } else {
                    set(e.getKey(), e.getValue());
                }
            });
        }
        return this;
    }

    static class StringValueConverter implements ValueConverter<String> {

        @Override
        public String convertObject(Object value) {
            if (value instanceof String) {
                return (String) value;
            }
            return value.toString();
        }

        @Override
        public String convertInt(int value) {
            return String.valueOf(value);
        }

        @Override
        public String convertLong(long value) {
            return String.valueOf(value);
        }

        @Override
        public String convertDouble(double value) {
            return String.valueOf(value);
        }

        @Override
        public String convertChar(char value) {
            return String.valueOf(value);
        }

        @Override
        public String convertBoolean(boolean value) {
            return String.valueOf(value);
        }

        @Override
        public String convertFloat(float value) {
            return String.valueOf(value);
        }

        @Override
        public boolean convertToBoolean(String value) {
            return Boolean.parseBoolean(value);
        }

        @Override
        public String convertByte(byte value) {
            return String.valueOf(value);
        }

        @Override
        public byte convertToByte(String value) {
            return Byte.parseByte(value);
        }

        @Override
        public char convertToChar(String value) {
            return value.charAt(0);
        }

        @Override
        public String convertShort(short value) {
            return String.valueOf(value);
        }

        @Override
        public short convertToShort(String value) {
            return Short.parseShort(value);
        }

        @Override
        public int convertToInt(String value) {
            return Integer.parseInt(value);
        }

        @Override
        public long convertToLong(String value) {
            return Long.parseLong(value);
        }

        @Override
        public String convertTimeMillis(long value) {
            return DateFormatter.format(new Date(value));
        }

        @Override
        public long convertToTimeMillis(String value) {
            Date date = DateFormatter.parseHttpDate(value);
            if (date == null) {
                PlatformDependent.throwException(new ParseException("header can't be parsed into a Date: " + value, 0));
                return 0;
            }
            return date.getTime();
        }

        @Override
        public float convertToFloat(String value) {
            return Float.parseFloat(value);
        }

        @Override
        public double convertToDouble(String value) {
            return Double.parseDouble(value);
        }

    }

}
