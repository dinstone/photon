package com.dinstone.photon.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.dinstone.photon.message.Response;

import io.netty.channel.Channel;
import io.netty.channel.local.LocalChannel;

public class UtilsTest {

	@Test
	public void attributeUtilTest() {
		Channel channel = new LocalChannel();
		Map<Integer, CompletableFuture<Response>> fs = AttributeUtil.futures(channel);
		assertNotNull(fs);
	}

	@Test
	public void futureTest() {
		final CompletableFuture<Response> promise = new CompletableFuture<>();
		CompletableFuture<String> rf = promise.thenApply(r -> "hello");
		rf.whenComplete((r, e) -> {
			assertNull(r);
			assertNotNull(e);
			e.printStackTrace();

		});
		promise.completeExceptionally(new RuntimeException("test"));
	}

	@Test
	public void futureTest2() {
		final CompletableFuture<Response> promise = new CompletableFuture<>();
		CompletableFuture<String> rf = promise.thenApply(r -> "code:" + r.getMsgId());
		rf.thenAccept(r -> System.out.println(r));
		rf.whenComplete((r, e) -> {
			assertNotNull(r);
			System.out.println("result is " + r);
		});
		Response r = new Response();
		r.setMsgId(19999);
		promise.complete(r);
	}

}
