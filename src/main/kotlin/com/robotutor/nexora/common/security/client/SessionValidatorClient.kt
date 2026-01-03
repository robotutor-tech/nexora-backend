package com.robotutor.nexora.common.security.client

import com.robotutor.nexora.common.webclient.WebClientWrapper
import com.robotutor.nexora.common.security.client.view.AccountSessionPrincipalResponse
import com.robotutor.nexora.common.security.client.view.ActorSessionPrincipalResponse
import com.robotutor.nexora.common.security.client.view.InternalSessionPrincipalResponse
import com.robotutor.nexora.common.security.client.view.SessionValidationResponse
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.domain.vo.SessionValidationResult
import com.robotutor.nexora.common.webclient.get
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.principal.InternalData
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SessionValidatorClient(private val webClient: WebClientWrapper, private val appConfig: AppConfig) {
    fun validate(token: String): Mono<SessionValidationResult> {
        return webClient.get<SessionValidationResponse>(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.validatePath,
            headers = mapOf(HttpHeaders.AUTHORIZATION to token)
        )
            .map {
                SessionValidationResult(
                    isValid = it.isValid,
                    expiresIn = it.expiresIn,
                    principalData = when (it.principal) {
                        is AccountSessionPrincipalResponse -> AccountData(
                            accountId = AccountId(it.principal.accountId),
                            type = it.principal.type,
                            principalId = PrincipalId(it.principal.principalId)
                        )

                        is ActorSessionPrincipalResponse -> ActorData(
                            actorId = ActorId(it.principal.actorId),
                            premisesId = PremisesId(it.principal.premisesId),
                            accountId = AccountId(it.principal.accountId),
                            type = it.principal.type,
                            principalId = PrincipalId(it.principal.principalId)
                        )

                        is InternalSessionPrincipalResponse -> InternalData(it.principal.id)
                    },
                )
            }
    }
}
