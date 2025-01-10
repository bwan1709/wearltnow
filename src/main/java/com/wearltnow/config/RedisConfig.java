package com.wearltnow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${redis.port}")
    private String redisPost;
    @Value("${redis.host}")
    private String redisHort;
    @Value("${spring.cache.redis.time-to-live}")
    private Duration ttl;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHort);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPost));

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    <K, V>RedisTemplate<K, V> redisTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    <K, F, V>HashOperations<K, F, V> hashOperations(RedisTemplate<K, V> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    public <K, V> void setWithDefaultTTL(RedisTemplate<K, V> redisTemplate, K key, V value) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }
}
