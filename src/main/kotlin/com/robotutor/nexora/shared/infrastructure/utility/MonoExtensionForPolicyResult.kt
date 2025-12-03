package com.robotutor.nexora.shared.infrastructure.utility

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.exception.PolicyViolationException
import reactor.core.publisher.Mono

fun Mono<PolicyResult>.errorOnDenied(
    errorCode: String,
    message: String
): Mono<PolicyResult> {
    return flatMap { policyResult ->
        if (policyResult.isAllowed()) {
            createMono(policyResult)
        } else {
            createMonoError(
                PolicyViolationException(
                    errorCode = errorCode,
                    message = "$message due to: ${policyResult.getReasons().joinToString(", ")}}"
                )
            )
        }
    }
}