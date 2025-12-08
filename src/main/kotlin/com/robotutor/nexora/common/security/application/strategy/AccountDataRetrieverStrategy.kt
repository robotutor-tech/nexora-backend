package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.AccountDataRetriever
import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.common.security.domain.vo.AccountPrincipalContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccountDataRetrieverStrategy(
    private val accountDataRetriever: AccountDataRetriever
) : DataRetrieverStrategy<AccountPrincipalContext, AccountData> {
    override fun getPrincipalData(context: AccountPrincipalContext): Mono<AccountData> {
        return accountDataRetriever.getAccountData(context.accountId)
    }
}