package org.redisson;

import org.redisson.api.MapOptions;
import org.redisson.api.RFuture;
import org.redisson.api.RTairMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.misc.CompletableFutureWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class RedissonTairMap<K, V> extends RedissonMap<K, V> implements RTairMap<K, V> {
    public RedissonTairMap(CommandAsyncExecutor commandExecutor, String name, RedissonClient redisson, MapOptions<K, V> options, WriteBehindService writeBehindService) {
        super(commandExecutor, name, redisson, options, writeBehindService);
    }

    public RedissonTairMap(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public RedissonTairMap(Codec codec, CommandAsyncExecutor commandExecutor, String name, RedissonClient redisson, MapOptions<K, V> options, WriteBehindService writeBehindService) {
        super(codec, commandExecutor, name, redisson, options, writeBehindService);
    }

    @Override
    public V addAndExGet(K key, Number value,long ttl) {
        return get(addAndExGetAsync(key, value,ttl));
    }

    @Override
    public RFuture<V> addAndExGetAsync(K key, Number value, long ttl) {
        checkKey(key);
        checkValue(value);

        RFuture<V> future = addAndExGetOperationAsync(key, value,ttl);
        if (hasNoWriter()) {
            return future;
        }

        return mapWriterFuture(future, new MapWriterTask.Add() {
            @Override
            public Map<K, V> getMap() {
                return Collections.singletonMap(key, commandExecutor.getNow(future.toCompletableFuture()));
            }
        });
    }

    @Override
    public final RFuture<V> exGetAsync(K key) {
        long threadId = Thread.currentThread().getId();
        return exGetAsync(key, threadId);
    }

    @Override
    public void exPut(K key, V value, long ttl) {
        get(putAsync(key, value, ttl));
    }

    @Override
    public V exGet(K key) {
        return get(exGetOperationAsync((K) key));
    }

    @Override
    public RFuture<V> putAsync(K key, V value, long ttl) {
        checkKey(key);
        checkValue(value);

        RFuture<V> future = putOperationAsync(key, value,ttl);
        if (hasNoWriter()) {
            return future;
        }

        return mapWriterFuture(future, new MapWriterTask.Add(key, value));
    }

    protected RFuture<V> putOperationAsync(K key, V value,long ttl) {
        String name = getRawName(key);
        return commandExecutor.writeAsync(name,new StringCodec(), RedisCommands.EXHSET,name,key,value,"EX",ttl);
    }

    protected RFuture<V> addAndExGetOperationAsync(K key, Number value,long ttl) {
        String name = getRawName(key);
        return commandExecutor.writeAsync(name,new StringCodec(),RedisCommands.EXHINCRBY,name,key,value,"EX",ttl);
    }

    public RFuture<V> exGetOperationAsync(K key) {
        String name = getRawName(key);
        return commandExecutor.readAsync(name,new StringCodec(),RedisCommands.EXHGET,name,key);
    }

    protected RFuture<V> exGetAsync(K key, long threadId) {
        checkKey(key);

        RFuture<V> future = exGetOperationAsync(key);
        if (hasNoLoader()) {
            return future;
        }

        CompletionStage<V> f = future.thenCompose(res -> {
            if (res == null) {
                return loadValue(key, false, threadId);
            }
            return CompletableFuture.completedFuture(res);
        });
        return new CompletableFutureWrapper<>(f);
    }
}
