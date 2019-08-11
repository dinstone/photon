package com.dinstone.photon.message;

import java.util.List;

public interface MultiMap<K, V> {
    /**
     * Returns the number of key-value pairs in this multimap.
     *
     * <p>
     * <b>Note:</b> this method does not return the number of <i>distinct keys</i>
     * in the multimap, which is given by {@code keySet().size()} or
     * {@code asMap().size()}. See the opening section of the {@link Multimap} class
     * documentation for clarification.
     */
    int size();

    /**
     * Returns {@code true} if this multimap contains no key-value pairs. Equivalent
     * to {@code size() == 0}, but can in some cases be more efficient.
     */
    boolean isEmpty();

    /**
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the key {@code key}.
     */
    boolean containsKey(Object key);

    /**
     * Set the key's value to be a one item list consisting of the supplied value.
     * Any existing values will be replaced.
     *
     * @param key
     *            the key
     * @param value
     *            the single value of the key
     */
    void putSingle(K key, V value);

    /**
     * Add a value to the current list of values for the supplied key.
     *
     * @param key
     *            the key
     * @param value
     *            the value to be added.
     */
    void add(K key, V value);

    /**
     * A shortcut to get the first value of the supplied key.
     *
     * @param key
     *            the key
     * @return the first value for the specified key or null if the key is not in
     *         the map.
     */
    V getFirst(K key);

    /**
     * Add multiple values to the current list of values for the supplied key. If
     * the supplied array of new values is empty, method returns immediately. Method
     * throws a {@code NullPointerException} if the supplied array of values is
     * {@code null}.
     *
     * @param key
     *            the key.
     * @param newValues
     *            the values to be added.
     * @throws NullPointerException
     *             if the supplied array of new values is {@code null}.
     * @since 2.0
     */
    void addAll(K key, V... newValues);

    /**
     * Add all the values from the supplied value list to the current list of values
     * for the supplied key. If the supplied value list is empty, method returns
     * immediately. Method throws a {@code NullPointerException} if the supplied
     * array of values is {@code null}.
     *
     * @param key
     *            the key.
     * @param valueList
     *            the list of values to be added.
     * @throws NullPointerException
     *             if the supplied value list is {@code null}.
     * @since 2.0
     */
    void addAll(K key, List<V> valueList);

    /**
     * Add a value to the first position in the current list of values for the
     * supplied key.
     *
     * @param key
     *            the key
     * @param value
     *            the value to be added.
     * @since 2.0
     */
    void addFirst(K key, V value);
}
