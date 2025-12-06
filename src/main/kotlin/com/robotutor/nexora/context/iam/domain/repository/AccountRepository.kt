package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountRepository {
    fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate>
}