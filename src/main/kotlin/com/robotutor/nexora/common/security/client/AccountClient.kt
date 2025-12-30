package com.robotutor.nexora.common.security.client

import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.client.view.AccountResponse
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import com.robotutor.nexora.common.httpclient.WebClientWrapper
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service()
class AccountClient(private val webClient: WebClientWrapper, private val appConfig: AppConfig) {
    fun getAccountData(accountId: AccountId): Mono<AccountData> {
        return webClient.get(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.accountPath,
            uriVariables = mapOf("accountId" to accountId.value),
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer ${appConfig.internalAccessToken}"),
            returnType = AccountResponse::class.java
        )
            .map { AccountData(AccountId(it.accountId), it.type, PrincipalId(it.principalId)) }
    }
}
