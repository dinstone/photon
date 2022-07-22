/*
 * Copyright (C) 2018~2022 dinstone<dinstone@163.com>
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
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

public class HeadersTest {

    @Test
    public void test() {
        Headers h = new Headers();
        h.add("name", "n1").add("name", "n2").add("name", "n3");

        String v = h.get("name");
        assertSame("n1", v);

        List<String> vs = h.getAll("name");
        assertEquals(3, vs.size());

        v = h.getAndRemove("name");
        assertSame("n1", v);

        vs = h.getAll("name");
        assertEquals(0, vs.size());

        h.add("name", "first").add("name", "second");
        h.set("name", "dinstone");
        v = h.get("name");
        assertSame("dinstone", v);

        vs = h.getAll("name");
        assertEquals(1, vs.size());
    }

    @Test
    public void testMM() {
        MultiValueMap<String, String> mvm = new MultiValueMap<>();
        mvm.add("seq", "one").add("seq", "two").add("seq", "three");

        String v = mvm.get("seq");
        assertEquals(v, "one");

        boolean b = mvm.remove("seq");
        assertEquals(b, true);
    }

}
