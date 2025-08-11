package com.robotutor.nexora.shared.adapters.outbound.cache.services

import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer
import com.robotutor.nexora.common.security.createFlux
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CacheService(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {
    val logger = Logger(this::class.java)
    fun <T : Any> retrieve(
        clazz: Class<T>,
        key: String = "key",
        ttlInSeconds: Long = 60,
        switchIfAbsent: () -> Mono<T>
    ): Mono<T> {
        return getRedisKey(key).flatMap { keyName ->
            getValue(keyName, clazz)
                .logOnSuccess(logger, "Successfully get value for $keyName")
                .logOnError(logger, "", "Failed to get value for $keyName")
                .switchIfEmpty(
                    switchIfAbsent()
                        .flatMap { setValue(keyName, it, ttlInSeconds) }
                        .logOnSuccess(logger, "Successfully set value for $keyName")
                        .logOnError(logger, "", "Failed to set value for $keyName")
                )
        }
    }

    fun <T : Any> retrieves(
        clazz: Class<T>,
        key: String = "key",
        ttlInSeconds: Long = 60,
        switchIfAbsent: () -> Flux<T>
    ): Flux<T> {
        return getRedisKey(key)
            .flatMapMany { keyName ->
                getValues(keyName, clazz)
                    .logOnSuccess(logger, "Successfully get values from list for $keyName")
                    .logOnError(logger, "", "Failed to get values from list for $keyName")
                    .switchIfEmpty(
                        switchIfAbsent()
                            .collectList()
                            .flatMapMany { setValueInList(keyName, it, ttlInSeconds) }
                            .logOnSuccess(logger, "Successfully set values in list for $keyName")
                            .logOnError(logger, "", "Failed to set values in list for $keyName")
                    )
            }
    }

    fun <T : Any> update(key: String, ttlInSeconds: Long = 60, getValueToUpdate: () -> Mono<T>): Mono<T> {
        return getRedisKey(key).flatMap { keyName ->
            getValueToUpdate()
                .flatMap { setValue(keyName, it, ttlInSeconds) }
                .logOnSuccess(logger, "Successfully updated value for $keyName")
                .logOnError(logger, "", "Failed to update value for $keyName")
        }
    }

    fun <T : Any> updates(key: String, ttlInSeconds: Long = 60, getValuesToUpdate: () -> Mono<List<T>>): Mono<List<T>> {
        return getRedisKey(key).flatMap { keyName ->
            evictList(keyName)
                .flatMap { getValuesToUpdate() }
                .flatMapMany { setValueInList(keyName, it, ttlInSeconds) }
                .logOnSuccess(logger, "Successfully set values in list for $keyName")
                .logOnError(logger, "", "Failed to set values in list for $keyName")
                .collectList()
        }
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
        return if (values.isNotEmpty()) {
            reactiveRedisTemplate.opsForList()
                .rightPushAll(key, values.map { value -> DefaultSerializer.serialize(value) })
                .flatMap { reactiveRedisTemplate.expire(key, Duration.ofSeconds(ttlInSeconds)) }
        } else {
            Mono.empty()
        }
            .flatMapMany { createFlux(values) }
    }

    private fun <T : Any> getValues(key: String, clazz: Class<T>): Flux<T> {
        return reactiveRedisTemplate.opsForList()
            .range(key, 0, -1)
            .map {
                DefaultSerializer.deserialize(it, clazz)
            }
    }
}