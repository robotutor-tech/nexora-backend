package com.robotutor.nexora.redis.services

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.logger.serializer.DefaultSerializer
import com.robotutor.nexora.security.createFlux
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CacheService(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {
    val logger = Logger(this::class.java)
    fun <T : Any> retrieve(
        key: String,
        clazz: Class<T>,
        ttlInSeconds: Long = 60,
        switchIfAbsent: () -> Mono<T>
    ): Mono<T> {
        return getValue(key, clazz)
            .logOnSuccess(logger, "Successfully get value for $key")
            .logOnError(logger, "", "Failed to get value for $key")
            .switchIfEmpty(
                switchIfAbsent()
                    .flatMap { setValue(key, it, ttlInSeconds) }
                    .logOnSuccess(logger, "Successfully set value for $key")
                    .logOnError(logger, "", "Failed to set value for $key")
            )
    }

    fun <T : Any> retrieves(
        key: String,
        clazz: Class<T>,
        ttlInSeconds: Long = 60,
        switchIfAbsent: () -> Mono<List<T>>
    ): Flux<T> {
        return getValues(key, clazz)
            .logOnSuccess(logger, "Successfully get values from list for $key")
            .logOnError(logger, "", "Failed to get values from list for $key")
            .switchIfEmpty(
                switchIfAbsent()
                    .flatMapMany { setValueInList(key, it, ttlInSeconds) }
                    .logOnSuccess(logger, "Successfully set values in list for $key")
                    .logOnError(logger, "", "Failed to set values in list for $key")
            )
    }

    fun <T : Any> update(key: String, ttlInSeconds: Long = 60, getValueToUpdate: () -> Mono<T>): Mono<T> {
        return getValueToUpdate()
            .flatMap { setValue(key, it, ttlInSeconds) }
            .logOnSuccess(logger, "Successfully updated value for $key")
            .logOnError(logger, "", "Failed to update value for $key")
    }

    fun <T : Any> updates(key: String, ttlInSeconds: Long = 60, getValuesToUpdate: () -> Mono<List<T>>): Flux<T> {
        return evictList(key)
            .flatMap { getValuesToUpdate() }
            .flatMapMany { setValueInList(key, it, ttlInSeconds) }
            .logOnSuccess(logger, "Successfully set values in list for $key")
            .logOnError(logger, "", "Failed to set values in list for $key")
    }

    fun evict(key: String): Mono<Boolean> {
        return reactiveRedisTemplate.opsForValue().delete(key)
            .logOnSuccess(logger, "Successfully clear value for $key")
            .logOnError(logger, "", "Failed to clear value for $key")
    }

    fun evictList(key: String): Mono<Boolean> {
        return reactiveRedisTemplate.opsForList().delete(key)
            .logOnSuccess(logger, "Successfully clear values from list for $key")
            .logOnError(logger, "", "Failed to clear values from list for $key")
    }

    private fun <T : Any> setValue(key: String, value: T, ttlInSeconds: Long = 60): Mono<T> {
        return reactiveRedisTemplate.opsForValue()
            .set(key, DefaultSerializer.serialize(value), Duration.ofSeconds(ttlInSeconds))
            .map { value }
    }

    private fun <T : Any> getValue(key: String, clazz: Class<T>): Mono<T> {
        return reactiveRedisTemplate.opsForValue().get(key).map {
            DefaultSerializer.deserialize(it, clazz)
        }
    }

    private fun <T : Any> setValueInList(key: String, values: List<T>, ttlInSeconds: Long = 60): Flux<T> {
        return reactiveRedisTemplate.opsForList()
            .rightPushAll(key, values.map { value -> DefaultSerializer.serialize(value) })
            .flatMap { reactiveRedisTemplate.expire(key, Duration.ofSeconds(ttlInSeconds)) }
            .flatMapMany { createFlux(values) }

    }

    private fun <T : Any> getValues(key: String, clazz: Class<T>): Flux<T> {
        return reactiveRedisTemplate.opsForList().range(key, 0, -1).map {
            DefaultSerializer.deserialize(it, clazz)
        }
    }
}