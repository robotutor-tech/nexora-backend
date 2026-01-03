package com.robotutor.nexora.common.security.aspect

import com.robotutor.nexora.common.security.client.AccessAuthorizerClient
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(0)
class ApplicationAuthorizeAspect(
    private val accessAuthorizer: AccessAuthorizerClient,
) {

//    @Around("@annotation(authorize)")
//    fun enforce(authorize: Authorize, pjp: ProceedingJoinPoint): Any {
//        val signature = pjp.signature as MethodSignature
//        val method = signature.method
//        val args = pjp.args
//
//        val returnType = signature.returnType
//        val resourceId = resourceIdResolver.resolve(method, args, authorize.selector)
//
//        return when {
//            Mono::class.java.isAssignableFrom(returnType) -> {
//                accessAuthorizer.authorize(authorize, resourceId)
//                    .flatMap { allowed ->
//                        if (!allowed) createMonoError(UnAuthorizedException(SecurityError.NEXORA0105))
//                        else (pjp.proceed() as Mono<*>)
//                    }
//            }
//
//            Flux::class.java.isAssignableFrom(returnType) -> {
//                accessAuthorizer.authorize(authorize, resourceId)
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
//    }
}
