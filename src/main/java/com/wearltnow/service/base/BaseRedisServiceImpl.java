package com.wearltnow.service.base;

import com.wearltnow.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BaseRedisServiceImpl<K, F, V> implements BaseRedisService<K, F, V> {

    @Autowired
    private RedisTemplate<K, V> redisTemplate;
    @Autowired
    private RedisConfig redisConfig;

    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(K key, long timeoutInDays) {
        redisTemplate.expire(key, timeoutInDays, java.util.concurrent.TimeUnit.DAYS);
    }

    @Override
    public void hashSet(K key, F field, V value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public boolean hashExists(K key, F field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<F, V> getField(K key) {
        return (Map<F, V>) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public V hashGet(K key, F field) {
        return (V) redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public List<V> hashGetByFieldPrefix(K key, F fieldPrefix) {
        Map<F, V> entries = (Map<F, V>) redisTemplate.opsForHash().entries(key);
        return entries.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(fieldPrefix.toString()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Set<F> getFieldPrefixes(K key) {
        return (Set<F>) redisTemplate.opsForHash().keys(key);
    }

    @Override
    public void delete(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delete(K key, F field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    @Override
    public void delete(K key, List<F> fields) {
        redisTemplate.opsForHash().delete(key, fields.toArray());
    }

    @Override
    public void saveDataWithTTL(String key, Object value, long ttlInSeconds) {
        redisTemplate.opsForValue().set((K) key, (V) value, Duration.ofSeconds(ttlInSeconds));
    }

}
