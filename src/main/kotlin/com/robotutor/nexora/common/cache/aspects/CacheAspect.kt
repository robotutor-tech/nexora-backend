package com.robotutor.nexora.common.cache.aspects

import com.robotutor.nexora.common.cache.annotation.Cache
import com.robotutor.nexora.common.cache.annotation.CacheEvicts
import com.robotutor.nexora.common.cache.service.CacheService
import com.robotutor.nexora.common.utils.MethodArgumentExpressionEvaluator
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.ParameterizedType

@Aspect
@Component
class CacheAspect(
    private val cacheService: CacheService,
    private val spELEvaluator: MethodArgumentExpressionEvaluator
) {
    @Around("@annotation(cache)")
    fun retrieve(pjp: ProceedingJoinPoint, cache: Cache): Any {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val returnType = (method.genericReturnType as ParameterizedType).actualTypeArguments.first()
        val keyName = if (cache.name.isNotBlank()) {
            spELEvaluator.evaluate(method, pjp.args, cache.name) as String
        } else {
            cache.keyGenerator.java
                .getDeclaredConstructor()
                .newInstance()
                .generate(method, *pjp.args)
        }

        return when (signature.returnType) {
            Mono::class.java -> {
                @Suppress("UNCHECKED_CAST")
                cacheService.retrieve(keyName, returnType as Class<Any>, cache.ttlInSeconds) {
                    (pjp.proceed() as Mono<Any>)
                }
            }

            Flux::class.java -> {
                @Suppress("UNCHECKED_CAST")
                cacheService.retrieves(keyName, returnType as Class<Any>, cache.ttlInSeconds) {
                    pjp.proceed() as Flux<Any>
                }
            }

            else -> throw IllegalStateException("Unsupported return type for caching: $returnType")
        }
    }

    @Around("@annotation(cacheEvicts)")
    fun evict(pjp: ProceedingJoinPoint, cacheEvicts: CacheEvicts): Any {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val returnType = (method.genericReturnType as ParameterizedType).actualTypeArguments.first()
        val keyNames = cacheEvicts.evicts.map { spELEvaluator.evaluate(method, pjp.args, it) as String }

        return when (signature.returnType) {
            Mono::class.java -> {
                @Suppress("UNCHECKED_CAST")
                cacheService.evict(keyNames)
                    .flatMap {
                        (pjp.proceed() as Mono<Any>)
                    }
            }

            Flux::class.java -> {
                @Suppress("UNCHECKED_CAST")
                cacheService.evict(keyNames)
                    .flatMapMany {
                        pjp.proceed() as Flux<Any>
                    }
            }

            else -> throw IllegalStateException("Unsupported return type for caching: $returnType")
        }
    }
}