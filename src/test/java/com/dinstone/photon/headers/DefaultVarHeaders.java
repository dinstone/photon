package com.dinstone.photon.headers;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import io.netty.handler.codec.Headers;

public class DefaultVarHeaders implements VarHeaders {

    @Override
    public String get(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String get(String name, String defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAndRemove(String name, String defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getAll(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getAllAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean getBoolean(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Byte getByte(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte getByte(String name, byte defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Character getChar(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public char getChar(String name, char defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Short getShort(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getShort(String name, short defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Integer getInt(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getInt(String name, int defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Long getLong(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLong(String name, long defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Float getFloat(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getFloat(String name, float defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Double getDouble(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDouble(String name, double defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Long getTimeMillis(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getTimeMillis(String name, long defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Boolean getBooleanAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getBooleanAndRemove(String name, boolean defaultValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Byte getByteAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte getByteAndRemove(String name, byte defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Character getCharAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public char getCharAndRemove(String name, char defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Short getShortAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getShortAndRemove(String name, short defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Integer getIntAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getIntAndRemove(String name, int defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Long getLongAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLongAndRemove(String name, long defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Float getFloatAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getFloatAndRemove(String name, float defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Double getDoubleAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDoubleAndRemove(String name, double defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Long getTimeMillisAndRemove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getTimeMillisAndRemove(String name, long defaultValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean contains(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(String name, String value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsObject(String name, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsBoolean(String name, boolean value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsByte(String name, byte value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsChar(String name, char value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsShort(String name, short value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsInt(String name, int value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsLong(String name, long value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsFloat(String name, float value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsDouble(String name, double value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsTimeMillis(String name, long value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<String> names() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders add(String name, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders add(String name, Iterable<? extends String> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders add(String name, String... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addObject(String name, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addObject(String name, Iterable<?> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addObject(String name, Object... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addBoolean(String name, boolean value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addByte(String name, byte value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addChar(String name, char value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addShort(String name, short value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addInt(String name, int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addLong(String name, long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addFloat(String name, float value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addDouble(String name, double value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders addTimeMillis(String name, long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders add(Headers<? extends String, ? extends String, ?> headers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders set(String name, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders set(String name, Iterable<? extends String> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders set(String name, String... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setObject(String name, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setObject(String name, Iterable<?> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setObject(String name, Object... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setBoolean(String name, boolean value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setByte(String name, byte value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setChar(String name, char value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setShort(String name, short value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setInt(String name, int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setLong(String name, long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setFloat(String name, float value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setDouble(String name, double value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setTimeMillis(String name, long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders set(Headers<? extends String, ? extends String, ?> headers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VarHeaders setAll(Headers<? extends String, ? extends String, ?> headers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public VarHeaders clear() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

}
