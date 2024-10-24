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

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.dinstone.photon.message.Response;
import io.netty.channel.Channel;
import io.netty.channel.local.LocalChannel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        CompletableFuture<String> rf = promise.thenApply(r -> "code:" + r.getSequence());
        rf.thenAccept(r -> System.out.println(r));
        rf.whenComplete((r, e) -> {
            assertNotNull(r);
            System.out.println("result is " + r);
        });
        Response r = new Response();
        r.setSequence(19999);
        promise.complete(r);
    }

    @Test
    public void futureTest3() {
        final CompletableFuture<Response> promise = new CompletableFuture<>();
        promise.completeExceptionally(new CancellationException("close"));
        promise.completeExceptionally(new CompletionException("test", null));

        try {
            promise.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
