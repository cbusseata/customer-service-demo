package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Value(value = "${spring.redis.ttl}")
    private Integer ttl;

    @Value(value = "${spring.redis.hostname}")
    private String hostname;

    /**
     * https://www.baeldung.com/spring-boot-redis-cache
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // default connection details for the Redis instance are localhost:6379
        return RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(ttl))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext
                    .SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer())
            );
    }

    /**
     * https://docs.spring.io/spring-data/redis/reference/redis/connection-modes.html
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(hostname, 6379));
    }
}
