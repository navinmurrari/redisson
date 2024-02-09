package org.redisson.api;

public interface RTairMap<K, V> extends RMap<K, V>,RTairMapAsync<K, V> {
    /**
     * Associates the specified value with the specified key in this map and set a time to live for the entry.
     * If the map previously contained a mapping for the key, the old value is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param ttl time to live in seconds
     */
    void exPut(K key, V value, long ttl);

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    V exGet(K key);

    /**
     * Atomically adds the given value to the current value by key and returns the new value.
     *
     * @param key key with which the specified value is to be associated
     * @param delta the value to add
     * @param ttl time to live in seconds
     * @return the new value
     */
    V addAndExGet(K key, Number delta,long ttl);

}
