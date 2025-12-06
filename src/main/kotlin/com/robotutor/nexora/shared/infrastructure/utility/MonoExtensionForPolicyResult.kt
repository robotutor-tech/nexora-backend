package com.robotutor.nexora.shared.infrastructure.utility

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.exception.PolicyViolationException
import com.robotutor.nexora.shared.domain.exception.ServiceError
import reactor.core.publisher.Mono

fun Mono<PolicyResult>.errorOnDenied(
    error: ServiceError
): Mono<PolicyResult> {
    return flatMap { policyResult ->
        if (policyResult.isAllowed()) {
            createMono(policyResult)
        } else {
            createMonoError(
                PolicyViolationException(
                    ErrorResponse(
                        errorCode = error.errorCode,
                        message = "${error.message} due to ${policyResult.getReasons().joinToString(", ")}}"
                    )
                )
            )
        }
    }
}