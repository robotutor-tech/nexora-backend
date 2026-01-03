package com.robotutor.nexora.common.cache.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class CacheConfig(private val connectionFactory: ReactiveRedisConnectionFactory) {

    @Value("\${spring.data.redis.host}")
    private lateinit var redisHost: String

    @Value("\${spring.data.redis.port}")
    private lateinit var redisPort: String

    @Primary
    @Bean
    fun connectionFactory(): ReactiveRedisConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration(redisHost, redisPort.toInt())
        return LettuceConnectionFactory(redisConfig)
    }

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, String> {
        val serializer = StringRedisSerializer()
        val redisSerializationContext = RedisSerializationContext.newSerializationContext<String, String>()
            .key(serializer)
            .value(serializer)
            .hashKey(serializer)
            .hashValue(serializer)
            .build()

        return ReactiveRedisTemplate(connectionFactory, redisSerializationContext)
    }
}
