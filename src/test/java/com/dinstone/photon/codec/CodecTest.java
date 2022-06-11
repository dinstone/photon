package com.dinstone.photon.codec;

import java.util.Queue;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.transport.CustomeDecoder;
import com.dinstone.photon.transport.CustomeEncoder;
import com.dinstone.photon.transport.TransportDecoder;
import com.dinstone.photon.transport.TransportEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)

@State(Scope.Thread)
public class CodecTest {

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder().include(CodecTest.class.getSimpleName()).warmupIterations(3)
                .measurementIterations(3).forks(2).build();

        new Runner(opt).run();
    }

    @Benchmark
    @Test
    public void custome() throws Exception {
        ChannelInitializer<EmbeddedChannel> i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new CustomeDecoder()).addLast(new CustomeEncoder());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);

        dotest(channel);
    }

    @Benchmark
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
            notice.setMsgId(j);
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
                // System.out.println("msgid = " + n.getMsgId());
            }
        });
    }
}
