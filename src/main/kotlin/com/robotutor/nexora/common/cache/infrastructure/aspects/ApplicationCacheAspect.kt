package com.robotutor.nexora.common.cache.infrastructure.aspects

import com.robotutor.nexora.shared.application.cache.annotation.CacheEvictBy
import com.robotutor.nexora.shared.application.cache.annotation.Cached
import com.robotutor.nexora.common.cache.infrastructure.resolvers.MethodArgumentSpringExpressionCacheKeyResolver
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoEmpty
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Infrastructure implementation for application-level caching annotations.
 *
 * Notes:
 * - Designed for reactive return types. For Flux, prefer caching at a higher level (e.g., return a list) to avoid
 *   changing stream semantics.
 * - Preserves Reactor Context using shared createMono helpers.
 */
@Aspect
@Component
class ApplicationCacheAspect(
    private val cacheManager: CacheManager,
    private val keyResolver: MethodArgumentSpringExpressionCacheKeyResolver,
) {

    @Around("@annotation(cached)")
    fun aroundCached(cached: Cached, pjp: ProceedingJoinPoint): Any {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val args = pjp.args

        val returnType = signature.returnType

        val key = try {
            keyResolver.resolve(method, args, cached.key)
        } catch (e: Exception) {
            if (cached.failOnKeyEvaluationError) throw e
            else "__invalid_key__"
        }

        val cache = cacheManager.getCache(cached.cacheName)
            ?: throw IllegalStateException("Cache '${cached.cacheName}' is not configured")

        return when {
            Mono::class.java.isAssignableFrom(returnType) -> {
                Mono.defer {
                    val cachedValue = cache.get(key)?.get()
                    if (cachedValue != null) {
                        // Preserve context
                        return@defer createMono(cachedValue)
                    }

                    @Suppress("UNCHECKED_CAST")
                    return@defer (pjp.proceed() as Mono<Any?>)
                        .flatMap { value ->
                            if (value == null) createMonoEmpty()
                            else createMono(value).doOnNext { cache.put(key, it) }
                        }
                }
            }

            Flux::class.java.isAssignableFrom(returnType) -> {
                // IMPORTANT: caching Flux by collecting into a List changes semantics (backpressure, streaming),
                // so we don't do it implicitly.
                throw IllegalStateException(
                    "@Cached is not supported for Flux return types because it would change stream semantics. " +
                        "Please return Mono<List<T>> or create a dedicated cached facade for this query. " +
                        "Found: ${returnType.name} at ${method.declaringClass.name}.${method.name}"
                )
            }

            else -> throw IllegalStateException(
                "@Cached can only be applied to reactive methods returning Mono. " +
                    "Found: ${returnType.name} at ${method.declaringClass.name}.${method.name}"
            )
        }
    }

    @Around("@annotation(evict)")
    fun aroundEvict(evict: CacheEvictBy, pjp: ProceedingJoinPoint): Any {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val args = pjp.args

        val key = keyResolver.resolve(method, args, evict.key)
        val cache = cacheManager.getCache(evict.cacheName)
            ?: throw IllegalStateException("Cache '${evict.cacheName}' is not configured")

        val proceed = { pjp.proceed() }

        fun evictNow() {
            cache.evictIfPresent(key)
        }

        val returnType = signature.returnType

        if (evict.beforeInvocation) {
            evictNow()
        }

        return when {
            Mono::class.java.isAssignableFrom(returnType) -> {
                @Suppress("UNCHECKED_CAST")
                (proceed() as Mono<Any?>)
                    .doOnSuccess { if (!evict.beforeInvocation) evictNow() }
            }

            Flux::class.java.isAssignableFrom(returnType) -> {
                (proceed() as Flux<*>)
                    .doOnComplete { if (!evict.beforeInvocation) evictNow() }
            }

            else -> {
                val out = proceed()
                if (!evict.beforeInvocation) evictNow()
                out
            }
        }
    }
}
