package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.AccountDataRetriever
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.infrastructure.facade.view.AccountResponse
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityUserClient")
class AccountClient(private val webClient: WebClientWrapper, private val appConfig: AppConfig) : AccountDataRetriever {
    override fun getAccountData(accountId: AccountId): Mono<AccountData> {
        return webClient.get(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.accountPath,
            uriVariables = mapOf("accountId" to accountId.value),
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer ${appConfig.internalAccessToken}"),
            returnType = AccountResponse::class.java
        )
            .map { AccountData(accountId = AccountId(it.accountId), type = it.type) }
    }
}
