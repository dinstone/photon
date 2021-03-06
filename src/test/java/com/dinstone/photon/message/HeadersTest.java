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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HeadersTest {

    @Test
    public void test() throws Exception {
        Headers attach = null;
        byte[] bs = Headers.encode(attach);
        assertEquals(4, bs.length);

        Headers a = Headers.decode(bs);
        assertEquals(a, null);

        attach = new Headers();
        bs = Headers.encode(attach);
        assertEquals(4, bs.length);

        a = Headers.decode(bs);
        assertEquals(a, null);

        attach.put(null, "null value");
        attach.put("", "empty value");
        attach.put("key", null);
        attach.put("ekey", "");
        bs = Headers.encode(attach);
        assertEquals(64, bs.length);

        a = Headers.decode(bs);
        assertEquals("null value", a.get(null));
        assertEquals("empty value", a.get(""));
        assertEquals(null, a.get("key"));
        assertEquals("", a.get("ekey"));
    }

}
