package com.cost.config;

public class LocalCacheExample {

    private static final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)  // 最大缓存数量
            .expireAfterWrite(30, TimeUnit.MINUTES)  // 写入后过期时间
            .build();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.getIfPresent(key);
    }

    public static void remove(String key) {
        cache.invalidate(key);
    }
}