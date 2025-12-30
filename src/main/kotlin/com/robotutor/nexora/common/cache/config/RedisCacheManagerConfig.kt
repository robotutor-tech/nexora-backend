package com.robotutor.nexora.common.cache.config

import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisCacheManagerConfig {

    /**
     * CacheManager used by Spring Cache annotations.
     *
     * Note: Spring Cache abstraction is not reactive, but backed by Redis (network I/O) via Lettuce.
     * For truly reactive caching per-signal, prefer an explicit reactive cache adapter.
     */
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        val serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(
            GenericJackson2JsonRedisSerializer()
        )

        val config = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(serializationPair)
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build()
    }
}
