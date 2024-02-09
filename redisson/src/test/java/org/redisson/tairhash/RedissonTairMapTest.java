package org.redisson.tairhash;

import org.junit.jupiter.api.*;
import org.redisson.BaseMapTest;
import org.redisson.RedisDockerTest;
import org.redisson.Redisson;
import org.redisson.api.RTairMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.Protocol;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.ExecutionException;

@Testcontainers
public class RedissonTairMapTest  {
    static GenericContainer<?> REDIS = new GenericContainer<>("tairmodule/tairhash:latest").withExposedPorts(6379);
    static RedissonClient redisson;

    @BeforeAll
    public static void setUp(){
        REDIS.start();
        Config config = new Config();
        config.setProtocol(Protocol.RESP2);
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:" + REDIS.getFirstMappedPort());
        redisson = Redisson.create(config);
    }

    @BeforeEach
    public void before() {
        redisson.getTairMap("test").clear();
    }

    @AfterAll
    public static void tearDown(){
        REDIS.stop();
    }

    @Test
    public void shouldAssociateValueWithKeyAndSetTTL() throws InterruptedException {
        RTairMap<String,String> tairMap = redisson.getTairMap("test");
        tairMap.exPut("key", "value", 3L);
        String value = tairMap.exGet("key");
        Assertions.assertEquals("value", value);
        Thread.sleep(3000);
        value = tairMap.exGet("key");
        Assertions.assertNull(value);
    }

    @Test
    public void shouldReturnNullWhenNoMappingForKey() {
        RTairMap<String,String> tairMap = redisson.getTairMap("test");
        String value = tairMap.exGet("key");
        Assertions.assertNull(value);
    }

    @Test
    public void shouldReturnValueWhenMappingExistsForKey() {
        RTairMap<String,String> tairMap = redisson.getTairMap("test");
        tairMap.exPut("key", "value", 5L);
        String value = tairMap.exGet("key");
        Assertions.assertEquals("value", value);
    }

    @Test
    public void shouldAddValueToCurrentValueAndReturnNewValue() throws InterruptedException {
        RTairMap<String,Long> tairMap = redisson.getTairMap("test");
        tairMap.exPut("key", 10l, 5L);
        Long result = tairMap.addAndExGet("key", 1, 3L);
        Assertions.assertEquals(11, result.longValue());
        Thread.sleep(3000);
        result = tairMap.exGet("key");
        Assertions.assertNull(result);
    }

    @Test
    public void shouldReturnDeltaValueWhenNoMappingForKeyInAddAndExGet() {
        RTairMap<String,Long> tairMap = redisson.getTairMap("test");
        Long result = tairMap.addAndExGet("key", 1, 5L);
        Assertions.assertEquals(1, result.longValue());
    }

}
