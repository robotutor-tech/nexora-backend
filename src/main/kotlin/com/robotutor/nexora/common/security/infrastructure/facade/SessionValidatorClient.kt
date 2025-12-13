package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.SessionValidator
import com.robotutor.nexora.common.security.domain.vo.SessionValidationResult
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.domain.vo.AccountPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.ActorPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.InternalPrincipalContext
import com.robotutor.nexora.common.security.infrastructure.facade.view.AccountSessionPrincipalResponse
import com.robotutor.nexora.common.security.infrastructure.facade.view.ActorSessionPrincipalResponse
import com.robotutor.nexora.common.security.infrastructure.facade.view.InternalSessionPrincipalResponse
import com.robotutor.nexora.common.security.infrastructure.facade.view.SessionValidationResponse
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.serializer.DefaultSerializer
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import io.jsonwebtoken.io.Deserializer
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SessionValidatorClient(private val webClient: WebClientWrapper, private val appConfig: AppConfig) :
    SessionValidator {
    override fun validate(token: String): Mono<SessionValidationResult> {
        return webClient.get(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.validatePath,
            headers = mapOf(HttpHeaders.AUTHORIZATION to token),
            returnType = SessionValidationResponse::class.java
        )
            .map{
                SessionValidationResult(
                    isValid = it.isValid,
                    expiresIn = it.expiresIn,
                    principal = when (it.principal) {
                        is AccountSessionPrincipalResponse -> AccountPrincipalContext(
                            accountId = AccountId(it.principal.accountId),
                            type = it.principal.accountType
                        )

                        is ActorSessionPrincipalResponse -> ActorPrincipalContext(
                            actorId = ActorId(it.principal.actorId),
                            premisesId = PremisesId(it.principal.premisesId),
                            accountId = AccountId(it.principal.accountId),
                            type = it.principal.accountType
                        )

                        is InternalSessionPrincipalResponse -> InternalPrincipalContext(it.principal.id)
                    },
                )
            }
    }
}
