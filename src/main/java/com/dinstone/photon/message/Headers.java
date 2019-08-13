package com.dinstone.photon.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Headers implements Map<String, String> {

    protected final Map<String, String> store;

    public Headers() {
        store = new HashMap<String, String>();
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return store.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return store.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return store.get(key);
    }

    @Override
    public String put(String key, String value) {
        return store.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public Set<String> keySet() {
        return store.keySet();
    }

    @Override
    public Collection<String> values() {
        return store.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return store.entrySet();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        store.putAll(m);
    }

}
