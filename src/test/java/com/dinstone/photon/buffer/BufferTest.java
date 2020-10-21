/*
 * Copyright (C) 2018~2020 dinstone<dinstone@163.com>
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
package com.dinstone.photon.buffer;

import java.util.Map.Entry;

import org.junit.Test;

import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.codec.MessageCodec;
import com.dinstone.photon.message.Headers;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.CharsetUtil;

public class BufferTest {

    @Test
    public void oneHotBufferTest() {
        Request request = new Request();
        request.setId(1);
        Headers headers = new Headers();
        headers.put("service", "com.dinstone.focus.example.DemoService");
        headers.put("method", "hello");
        headers.put("rpc.codec", "json");

        request.setHeaders(headers);
        request.setTimeout(10000);
        request.setContent(
                "Hello World, this is buffer test message,Hello World,Hello World, this is buffer test message,Hello World, Hello World, this is buffer test message,Hello World,  this is buffer test message,Hello World, this is buffer test message"
                        .getBytes());

        long s = System.currentTimeMillis();

        int c = 0;
        while (c < 1000000) {
            ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();

            encodeMessage(request, out);

            out.release();

            c++;
        }

        long e = System.currentTimeMillis();

        System.out.println("one hot take " + (e - s) + " ms");
    }

    @Test
    public void oneBufferTest() {
        Request request = new Request();
        request.setId(1);
        Headers headers = new Headers();
        headers.put("service", "com.dinstone.focus.example.DemoService");
        headers.put("method", "hello");
        headers.put("rpc.codec", "json");

        request.setHeaders(headers);
        request.setTimeout(10000);
        request.setContent(
                "Hello World, this is buffer test message,Hello World,Hello World, this is buffer test message,Hello World, Hello World, this is buffer test message,Hello World,  this is buffer test message,Hello World, this is buffer test message"
                        .getBytes());

        long s = System.currentTimeMillis();

        int c = 0;
        while (c < 1000000) {
            ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();

            encodeMessage(request, out);

            out.release();

            c++;
        }

        long e = System.currentTimeMillis();

        System.out.println("one exe take " + (e - s) + " ms");
    }

    private void encodeMessage(Request message, ByteBuf out) {
        int s = out.writerIndex();

        out.writeBytes(new byte[4]);
        out.writeByte(message.getType().getValue());
        out.writeByte(message.getVersion());
        out.writeInt(message.getId());
        out.writeInt(message.getTimeout());

        // headers
        writeHeaders(out, message.getHeaders());
        // content
        writeContent(out, message.getContent());

        int len = out.writerIndex() - s - 4;

        out.setInt(s, len);

    }

    @Test
    public void towHotBufferTest() {
        Request request = new Request();
        request.setId(1);
        Headers headers = new Headers();
        headers.put("service", "com.dinstone.focus.example.DemoService");
        headers.put("method", "hello");
        headers.put("rpc.codec", "json");

        request.setHeaders(headers);
        request.setTimeout(10000);
        request.setContent(
                "Hello World, this is buffer test message,Hello World,Hello World, this is buffer test message,Hello World, Hello World, this is buffer test message,Hello World,  this is buffer test message,Hello World, this is buffer test message"
                        .getBytes());

        long s = System.currentTimeMillis();

        int c = 0;
        while (c < 1000000) {
            ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();

            ByteBuf buffer = encodeMessage(request);
            int length = buffer.readableBytes();
            out.writeInt(length);
            out.writeBytes(buffer);

            buffer.release();
            out.release();

            c++;
        }

        long e = System.currentTimeMillis();

        System.out.println("tow hot take " + (e - s) + " ms");
    }

    @Test
    public void towBufferTest() {
        Request request = new Request();
        request.setId(1);
        Headers headers = new Headers();
        headers.put("service", "com.dinstone.focus.example.DemoService");
        headers.put("method", "hello");
        headers.put("rpc.codec", "json");

        request.setHeaders(headers);
        request.setTimeout(10000);
        request.setContent(
                "Hello World, this is buffer test message,Hello World,Hello World, this is buffer test message,Hello World, Hello World, this is buffer test message,Hello World,  this is buffer test message,Hello World, this is buffer test message"
                        .getBytes());

        long s = System.currentTimeMillis();

        int c = 0;
        while (c < 1000000) {
            ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();

            ByteBuf buffer = encodeMessage(request);
            int length = buffer.readableBytes();
            out.writeInt(length);
            out.writeBytes(buffer);

            buffer.release();
            out.release();

            c++;
        }

        long e = System.currentTimeMillis();

        System.out.println("tow exe take " + (e - s) + " ms");
    }

    protected void writeString(ByteBuf buff, String str) {
        byte[] strBytes = str.getBytes(CharsetUtil.UTF_8);
        buff.writeInt(strBytes.length);
        buff.writeBytes(strBytes);
    }

    protected String readString(ByteBuf in) {
        byte[] content = new byte[in.readInt()];
        in.readBytes(content);
        return new String(content, CharsetUtil.UTF_8);
    }

    protected byte[] readContent(ByteBuf in) {
        int len = in.readInt();
        if (len > 0) {
            byte[] content = new byte[len];
            in.readBytes(content);
            return content;
        }
        return null;
    }

    protected void writeContent(ByteBuf out, byte[] content) {
        if (content != null && content.length > 0) {
            out.writeInt(content.length);
            out.writeBytes(content);
        } else {
            out.writeInt(0);
        }
    }

    protected Headers readHeaders(ByteBuf in) {
        int count = in.readInt();
        if (count > 0) {
            Headers headers = new Headers();
            for (int i = 0; i < count; i++) {
                String key = readString(in);
                String value = readString(in);
                headers.put(key, value);
            }
            return headers;
        }

        return null;
    }

    protected void writeHeaders(ByteBuf out, Headers headers) {
        if (headers == null || headers.isEmpty()) {
            out.writeInt(0);
        } else {
            // count
            out.writeInt(headers.size());
            for (Entry<String, String> element : headers.entrySet()) {
                writeString(out, element.getKey());
                writeString(out, element.getValue());
            }
        }
    }

    public static ByteBuf encodeMessage(Message message) {
        MessageCodec<Message> codec = CodecManager.find(message.getType());
        if (codec != null) {
            return codec.encode(message);
        } else {
            throw new IllegalStateException("can't find message codec for " + message.getType());
        }
    }

    public static Message decodeMessage(ByteBuf in) {
        in.markReaderIndex();
        Message.Type messageType = Message.Type.valueOf(in.readByte());
        MessageCodec<Message> codec = CodecManager.find(messageType);
        if (codec != null) {
            in.resetReaderIndex();
            return codec.decode(in);
        } else {
            throw new IllegalStateException("can't find message codec for " + messageType);
        }
    }
}
