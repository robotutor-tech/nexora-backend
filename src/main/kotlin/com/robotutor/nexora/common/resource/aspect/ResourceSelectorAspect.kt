package com.robotutor.nexora.common.resource.aspect

import com.robotutor.nexora.common.resource.annotation.ResourceSelector
import com.robotutor.nexora.common.security.client.AccessAuthorizerClient
import com.robotutor.nexora.common.security.utils.MethodArgumentSpringExpressionResourceIdResolver
import com.robotutor.nexora.common.security.domain.exception.SecurityError
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.utility.createMonoError
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.lang.reflect.Parameter

@Aspect
@Component
@Order(0)
class ResourceSelectorAspect(
    private val accessAuthorizer: AccessAuthorizerClient,
    private val resourceIdResolver: MethodArgumentSpringExpressionResourceIdResolver,
) {

    @Around("@annotation(resourceSelector)")
    fun enforce(resourceSelector: ResourceSelector, pjp: ProceedingJoinPoint): Mono<Resources> {
        val signature = pjp.signature
        val method = signature.name
        val args = pjp.args
        println("----$signature-------$method----$args------")
        return Resources(
            premisesId = PremisesId(""),
            resourceType = resourceSelector.resourceType,
            actionType = resourceSelector.action,
            resourceSelector = com.robotutor.nexora.shared.domain.vo.ResourceSelector.ALL,
            allowedIds = emptySet(),
            deniedIds = emptySet()
        ).toMono()

//        val returnType = signature.returnType
//        val resourceId = resourceIdResolver.resolve(method, args, resourceSelector.selector)
//
//        return when {
//            Mono::class.java.isAssignableFrom(returnType) -> {
//                accessAuthorizer.authorize(resourceSelector, resourceId)
//                    .flatMap { allowed ->
//                        if (!allowed) createMonoError(UnAuthorizedException(SecurityError.NEXORA0105))
//                        else (pjp.proceed() as Mono<*>)
//                    }
//            }
//
//            Flux::class.java.isAssignableFrom(returnType) -> {
//                accessAuthorizer.authorize(resourceSelector, resourceId)
//                    .flatMapMany { allowed ->
//                        if (!allowed) createMonoError<Any>(UnAuthorizedException(SecurityError.NEXORA0105)).flux()
//                        else (pjp.proceed() as Flux<*>)
//                    }
//            }
//
//            else -> {
//                throw IllegalStateException(
//                    "@Authorize can only be applied to reactive methods returning Mono/Flux. " +
//                        "Found: ${returnType.name} at ${method.declaringClass.name}.${method.name}"
//                )
//            }
//        }
    }
}


