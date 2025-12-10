package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.TokenValidator
import com.robotutor.nexora.common.security.domain.vo.TokenValidationResult
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.domain.vo.AccountPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.ActorPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.InternalPrincipalContext
import com.robotutor.nexora.common.security.infrastructure.facade.view.AccountTokenPrincipalContextResponse
import com.robotutor.nexora.common.security.infrastructure.facade.view.ActorTokenPrincipalContextResponse
import com.robotutor.nexora.common.security.infrastructure.facade.view.InternalTokenPrincipalContextResponse
import com.robotutor.nexora.common.security.infrastructure.facade.view.TokenValidationResponse
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TokenValidatorClient(private val webClient: WebClientWrapper, private val appConfig: AppConfig) : TokenValidator {
    override fun validate(token: String): Mono<TokenValidationResult> {
        return webClient.get(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.validatePath,
            headers = mapOf(HttpHeaders.AUTHORIZATION to token),
            returnType = TokenValidationResponse::class.java
        )
            .map {
                TokenValidationResult(
                    isValid = it.isValid,
                    expiresIn = it.expiresIn,
                    principal = when (it.principal) {
                        is AccountTokenPrincipalContextResponse -> AccountPrincipalContext(
                            accountId = AccountId(it.principal.accountId),
                            type = it.principal.type
                        )

                        is ActorTokenPrincipalContextResponse -> ActorPrincipalContext(
                            actorId = it.principal.actorId,
                            roleId = it.principal.roleId
                        )

                        is InternalTokenPrincipalContextResponse -> InternalPrincipalContext(it.principal.id)
                    },
                )
            }
    }
}
