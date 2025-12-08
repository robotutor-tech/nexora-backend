package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.AccountId
import reactor.core.publisher.Mono

interface AccountDataRetriever {
    fun getAccountData(accountId: AccountId): Mono<AccountData>
}