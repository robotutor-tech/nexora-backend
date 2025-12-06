package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.shared.domain.vo.AccountId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountIdGenerator {
    fun generate(): Mono<AccountId>
}