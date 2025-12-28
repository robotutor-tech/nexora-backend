package com.robotutor.nexora.common.security.application.aspects

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.application.resolvers.MethodArgumentSpringExpressionResourceIdResolver
import com.robotutor.nexora.common.security.domain.exception.SecurityError
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.utility.createMonoError
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Enforces @Authorize on application services / use cases.
 *
 * Supports selectors like "#command.deviceId" via SpEL against method arguments.
 */
@Aspect
@Component
class ApplicationAuthorizeAspect(
    private val accessAuthorizer: AccessAuthorizer,
    private val resourceIdResolver: MethodArgumentSpringExpressionResourceIdResolver,
) {

    @Around("@annotation(authorize)")
    fun enforce(authorize: Authorize, pjp: ProceedingJoinPoint): Any {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val args = pjp.args

        val returnType = signature.returnType
        val resourceId = resourceIdResolver.resolve(method, args, authorize.selector)
        val httpAuthorize = HttpAuthorize(
            action = authorize.action,
            resource = authorize.resource,
            selector = authorize.selector,
        )

        return when {
            Mono::class.java.isAssignableFrom(returnType) -> {
                accessAuthorizer.authorize(httpAuthorize, resourceId)
                    .flatMap { allowed ->
                        if (!allowed) createMonoError(UnAuthorizedException(SecurityError.NEXORA0105))
                        else (pjp.proceed() as Mono<*>)
                    }
            }

            Flux::class.java.isAssignableFrom(returnType) -> {
                accessAuthorizer.authorize(httpAuthorize, resourceId)
                    .flatMapMany { allowed ->
                        if (!allowed) createMonoError<Any>(UnAuthorizedException(SecurityError.NEXORA0105)).flux()
                        else (pjp.proceed() as Flux<*>)
                    }
            }

            else -> {
                throw IllegalStateException(
                    "@Authorize can only be applied to reactive methods returning Mono/Flux. " +
                        "Found: ${returnType.name} at ${method.declaringClass.name}.${method.name}"
                )
            }
        }
    }
}
