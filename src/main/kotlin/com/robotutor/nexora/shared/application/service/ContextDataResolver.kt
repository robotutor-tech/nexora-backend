package com.robotutor.nexora.shared.application.service

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import reactor.core.publisher.Mono

object ContextDataResolver {
    fun getActorData(): Mono<ActorData> {
        return Mono.deferContextual { context ->
            val optional = context.getOrEmpty<ActorData>(ActorData::class.java)
            if (optional.isPresent) {
                createMono(optional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0101))
            }
        }
    }

    fun getAccountData(): Mono<AccountData> {
        return Mono.deferContextual { context ->
            val accountDataDataOptional = context.getOrEmpty<AccountData>(AccountData::class.java)
            if (accountDataDataOptional.isPresent) {
                createMono(accountDataDataOptional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0102))
            }
        }
    }
}