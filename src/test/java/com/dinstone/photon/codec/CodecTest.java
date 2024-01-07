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
package com.dinstone.photon.codec;

import java.util.Queue;

import org.junit.Test;

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

//@State(Scope.Thread)
public class CodecTest {

    public static void main(String[] args) throws Exception {
        // Options opt = new OptionsBuilder().include(CodecTest.class.getSimpleName()).warmupIterations(3)
        // .measurementIterations(3).forks(2).build();
        //
        // new Runner(opt).run();
    }

    // @Benchmark
    @Test
    public void custome() throws Exception {
        ChannelInitializer<EmbeddedChannel> i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageDecoder()).addLast(new MessageEncoder());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);

        dotest(channel);
    }

    // @Benchmark
    @Test
    public void transport() throws Exception {
        ChannelInitializer<EmbeddedChannel> i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new TransportDecoder()).addLast(new TransportEncoder());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);

        dotest(channel);
    }

    private void dotest(EmbeddedChannel channel) {
        for (int j = 0; j < 10; j++) {
            Notice notice = new Notice();
            notice.setSequence(j + 1);
            notice.setAddress("address " + j);
            notice.setContent(("count-" + j).getBytes());
            channel.writeOneOutbound(notice);
        }
        channel.flushOutbound();

        Queue<Object> ou = channel.outboundMessages();
        ou.forEach(o -> {
            // System.out.println("out: " + o);
            ByteBuf bb = (ByteBuf) o;
            channel.writeOneInbound(bb);
        });
        channel.flushInbound();

        Queue<Object> in = channel.inboundMessages();
        in.forEach(o -> {
            // System.out.println(" in = " + o);
            if (o instanceof Notice) {
                Notice n = (Notice) o;
                // System.out.println("sequence = " + n.getSequence());
            }
        });
    }
}
