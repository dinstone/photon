package com.dinstone.photon.message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HeaderMap implements MultiMap<String, String> {

    protected final Map<String, List<String>> store;

    public HeaderMap() {
        store = new HashMap<String, List<String>>();
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
    public void putSingle(String key, String value) {
        List<String> values = getValues(key);

        values.clear();
        if (value != null) {
            values.add(value);
        } else {
            addNull(values);
        }
    }

    protected void addNull(List<String> values) {
        // do nothing in the default implementation; ignore the null value
    }

    protected final List<String> getValues(String key) {
        List<String> l = store.get(key);
        if (l == null) {
            l = new LinkedList<String>();
            store.put(key, l);
        }
        return l;
    }

    @Override
    public void add(String key, String value) {
        List<String> values = getValues(key);
        if (value != null) {
            values.add(value);
        } else {
            addNull(values);
        }
    }

    @Override
    public String getFirst(String key) {
        List<String> values = store.get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void addAll(String key, String... newValues) {
        if (newValues == null) {
            throw new NullPointerException("Supplied array of values must not be null.");
        }
        if (newValues.length == 0) {
            return;
        }

        List<String> values = getValues(key);

        for (String value : newValues) {
            if (value != null) {
                values.add(value);
            } else {
                addNull(values);
            }
        }
    }

    @Override
    public void addAll(String key, List<String> valueList) {
        if (valueList == null) {
            throw new NullPointerException("Supplied list of values must not be null.");
        }
        if (valueList.isEmpty()) {
            return;
        }

        List<String> values = getValues(key);

        for (String value : valueList) {
            if (value != null) {
                values.add(value);
            } else {
                addNull(values);
            }
        }
    }

    @Override
    public void addFirst(String key, String value) {
        List<String> values = getValues(key);

        if (value != null) {
            values.add(0, value);
        } else {
            addFirstNull(values);
        }
    }

    protected void addFirstNull(List<String> values) {
        // do nothing in the default implementation; ignore the null value
    }
}
