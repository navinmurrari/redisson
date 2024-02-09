package org.redisson.api;

public interface RTairMapAsync<K, V> extends RMapAsync<K, V> {

    /**
     * Asynchronously associates the specified value with the specified key in this map and sets a time to live for the entry.
     * If the map previously contained a mapping for the key, the old value is replaced by the specified value.
     * The operation is completed when the method returns a RFuture.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param ttl   time to live for the entry in milliseconds
     * @return a RFuture that will be completed with the previous value associated with the key or null if there was no mapping for the key
     */
    RFuture<V> putAsync(K key, V value, long ttl);

    /**
     * Asynchronously retrieves the value to which the specified key is mapped.
     * The operation is completed when the method returns a RFuture.
     *
     * @param key the key whose associated value is to be returned
     * @return a RFuture that will be completed with the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key
     */
    RFuture<V> exGetAsync(K key);

    /**
     * Asynchronously adds the given number to the current value associated with the specified key in this map and sets a time to live for the entry.
     * The operation is completed when the method returns a RFuture.
     *
     * @param key   the key whose associated value is to be incremented
     * @param delta the number to be added to the current value
     * @param ttl   time to live for the entry in milliseconds
     * @return a RFuture that will be completed with the value to which the specified key is mapped after the addition,
     * or null if this map contains no mapping for the key
     */
    RFuture<V> addAndExGetAsync(K key, Number delta, long ttl);
}
