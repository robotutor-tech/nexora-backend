package com.robotutor.nexora.common.cache.service

import reactor.core.publisher.Mono

inline fun <reified T : Any> CacheService.retrieve(
    key: String,
    ttlInSeconds: Long = 60,
    noinline switchIfAbsent: () -> Mono<T>,
): Mono<T> = this.retrieve(key, T::class.java, ttlInSeconds, switchIfAbsent)