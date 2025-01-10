package com.wearltnow.service.base;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface BaseRedisService<K, F, V> {
    void set(K key, V value);

    void setTimeToLive(K key, long timeoutInDays);

    void hashSet(K key, F field, V value);

    boolean hashExists(K key, F field);

    Object get(K key);

    Map<F, V> getField(K key);

    V hashGet(K key, F field);

    List<V> hashGetByFieldPrefix(K key, F fieldPrefix);

    Set<F> getFieldPrefixes(K key);

    void delete(K key);

    void delete(K key, F field);

    void delete(K key, List<F> fields);

    void saveDataWithTTL(String key, Object value, long ttlInSeconds);
}
