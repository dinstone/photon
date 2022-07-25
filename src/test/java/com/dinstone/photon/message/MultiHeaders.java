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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MultiHeaders implements Iterable<Entry<String, String>> {

    protected final Map<String, List<String>> store = new HashMap<>();

    public MultiHeaders add(String name, String value) {
        List<String> vs = store.get(name);
        if (vs == null) {
            vs = new LinkedList<>();
            store.put(name, vs);
        }
        vs.add(value);
        return this;
    }

    public String get(String name) {
        List<String> vs = store.get(name);
        if (vs != null && vs.size() > 0) {
            return vs.get(0);
        }
        return null;
    }

    public String remove(String name) {
        String v = get(name);
        store.remove(name);
        return v;
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        return new HeaderIterator(this);
    }

    private final class HeaderIterator implements Iterator<Map.Entry<String, String>> {

        private Iterator<Element> iterator;

        public HeaderIterator(MultiHeaders multiHeaders) {
            List<Element> l = new LinkedList<>();
            for (Entry<String, List<String>> entry : multiHeaders.store.entrySet()) {
                if (entry.getValue() != null) {
                    String key = entry.getKey();
                    for (String val : entry.getValue()) {
                        l.add(new Element(key, val));
                    }
                }
            }
            iterator = l.iterator();
        }

        @Override
        public Entry<String, String> next() {
            return iterator.next();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        private final class Element implements Entry<String, String> {

            String key;
            String val;

            public Element(String key, String val) {
                super();
                this.key = key;
                this.val = val;
            }

            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException("setValue");
            }

            @Override
            public String getValue() {
                return val;
            }

            @Override
            public String getKey() {
                return key;
            }
        }
    }

}
