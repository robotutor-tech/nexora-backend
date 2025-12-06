package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.TokenValidator
import com.robotutor.nexora.common.security.domain.model.ValidateTokenResult
import com.robotutor.nexora.context.iam.interfaces.controller.AccountController
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityAuthValidatorClient")
class AuthTokenValidator(private val authController: AccountController) : TokenValidator {
    override fun validate(token: String): Mono<ValidateTokenResult> {
        return Mono.empty()
//        return authController.validate(token)
//            .map {
//                ValidateTokenResult(
//                    isValid = it.isValid,
//                    principal = PrincipalContextMapper.toPrincipalContext(it.principal),
//                    principalType = it.principalType,
//                    expiresIn = it.expiresIn,
//                )
//            }
    }
}
