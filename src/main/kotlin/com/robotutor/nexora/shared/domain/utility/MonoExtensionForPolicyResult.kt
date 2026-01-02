package com.robotutor.nexora.shared.domain.utility

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.exception.PolicyViolationException
import com.robotutor.nexora.shared.domain.exception.ServiceError
import com.robotutor.nexora.shared.domain.policy.Policy
import reactor.core.publisher.Mono

fun <T, C> Mono<T>.enforcePolicy(
    policy: Policy<C>,
    mapper: (T) -> C,
    error: ServiceError,
): Mono<T> {
    return flatMap { result ->
        evaluatePolicy(policy, mapper(result), error)
            .map { result }
    }
}

fun <C> evaluatePolicy(
    policy: Policy<C>,
    value: C,
    error: ServiceError
): Mono<PolicyResult> {
    val policyResult = policy.evaluate(value)
    return if (!policyResult.isAllowed()) {
        val errorResponse = ErrorResponse(
            error.errorCode,
            "${error.message} due to: ${policyResult.getReasons().joinToString(", ")}"
        )
        createMonoError(PolicyViolationException(errorResponse))
    } else {
        createMono(policyResult)
    }
}