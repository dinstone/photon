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
package com.dinstone.photon.message;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class MultiValueMap<K, V> {

    private final LinkedList<Element<K, V>>[] buckets;

    private final byte hashMask;

    private int size;

    private Element<K, V> head;

    @SuppressWarnings("unchecked")
    public MultiValueMap() {
        buckets = new LinkedList[8];
        hashMask = (byte) (buckets.length - 1);
        head = new Element<K, V>(0, null, null);
    }

    public V get(K name) {
        int h = name.hashCode();
        int i = index(h);
        V value = null;
        LinkedList<Element<K, V>> b = buckets[i];
        if (b != null) {
            for (Element<K, V> e : b) {
                if (e.hash == h && e.key.equals(name)) {
                    value = e.value;
                    break;
                }
            }
        }
        return value;
    }

    public MultiValueMap<K, V> add(K name, V value) {
        checkNotNull(value, "value");
        int h = name.hashCode();
        int i = index(h);
        add0(h, i, name, value);
        return this;
    }

    private void add0(int h, int i, K name, V value) {
        Element<K, V> ne = new Element<K, V>(h, name, value);
        LinkedList<Element<K, V>> b = buckets[i];
        if (b == null) {
            b = new LinkedList<>();
            buckets[i] = b;
        }
        b.add(ne);

        ne.before = head;
        ne.after = head.after;

        head.after = ne;

        size++;
    }

    public boolean remove(K name) {
        int h = name.hashCode();
        int i = index(h);
        return remove0(h, i, name) != null;
    }

    private V remove0(int h, int i, K name) {
        LinkedList<Element<K, V>> b = buckets[i];
        if (b == null) {
            return null;
        }

        V value = null;
        for (Iterator<Element<K, V>> iterator = b.iterator(); iterator.hasNext();) {
            Element<K, V> e = (Element<K, V>) iterator.next();
            if (e.hash == h && e.key.equals(name)) {
                value = e.value;
                iterator.remove();
                size--;
            }
        }
        return value;
    }

    private int index(int hash) {
        return hash & hashMask;
    }

    protected static class Element<K, V> implements Map.Entry<K, V> {
        protected final int hash;
        protected final K key;
        protected V value;
        /**
         * Overall insertion order linked list
         */
        protected Element<K, V> before;
        protected Element<K, V> after;

        protected Element(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public final Element<K, V> before() {
            return before;
        }

        public final Element<K, V> after() {
            return after;
        }

        protected void remove() {
            before.after = after;
            after.before = before;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final V setValue(V value) {
            checkNotNull(value, "value");
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Element other = (Element) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

    }
}
