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
package com.dinstone.photon.buffer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.dinstone.photon.codec.NoticeCodec;
import com.dinstone.photon.message.Headers;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

public class BufferTest {

    @Test
    public void customBuffer() {
        byte[] bs = "123adfads".getBytes();

        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        out.writeInt(bs.length);
        out.writeBytes(bs);

        common(out);
        common(out);

        perfect(out);
        perfect(out);
    }

    private void perfect(ByteBuf out) {
        long s = System.currentTimeMillis();
        int c = 0;
        while (c < 1000000) {
            out.markReaderIndex();
            int len = out.readInt();
            if (len > 0) {
                ByteBufUtil.getBytes(out, out.readerIndex(), len);
            }
            out.resetReaderIndex();

            c++;
        }
        long e = System.currentTimeMillis();
        System.out.println("perfect " + (e - s) + " ms");
    }

    private void common(ByteBuf out) {
        long s = System.currentTimeMillis();
        int c = 0;
        while (c < 1000000) {
            out.markReaderIndex();
            int len = out.readInt();
            if (len > 0) {
                byte[] content = new byte[len];
                out.readBytes(content);
            }
            out.resetReaderIndex();

            c++;
        }
        long e = System.currentTimeMillis();
        System.out.println("common " + (e - s) + " ms");
    }

    @Test
    public void noticTest() throws Exception {
        Notice notice = new Notice();
        notice.setMsgId(123);
        notice.setAddress("");
        notice.setContent("123adfads".getBytes());

        NoticeCodec nc = new NoticeCodec();
        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        nc.encode(notice, out);

        List<Object> ol = new ArrayList<Object>();
        nc.decode(out, ol);

        assertEquals(1, ol.size());
    }

    @Test
    public void oneHotBufferTest() throws Exception {
        Request request = new Request();
        request.setMsgId(1);

        request.setHeaders(null);
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
    public void oneBufferTest() throws Exception {
        Request request = new Request();
        request.setMsgId(1);
        request.setHeaders(new Headers());

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

    private void encodeMessage(Message message, ByteBuf out) throws Exception {
        message.encode(out);
    }

    @Test
    public void towHotBufferTest() throws Exception {
        Request request = new Request();
        request.setMsgId(1);
        request.setHeaders(null);
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
    public void towBufferTest() throws Exception {
        Request request = new Request();
        request.setMsgId(1);
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

    public static ByteBuf encodeMessage(Message message) throws Exception {
        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        message.encode(out);
        return out;
    }

    public static Message decodeMessage(ByteBuf in) throws Exception {
        in.markReaderIndex();
        Message m = Message.create(in.readByte(), in.readByte());
        in.resetReaderIndex();
        m.decode(in);
        return m;
    }
}
