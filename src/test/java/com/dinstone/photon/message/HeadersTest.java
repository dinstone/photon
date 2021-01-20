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
