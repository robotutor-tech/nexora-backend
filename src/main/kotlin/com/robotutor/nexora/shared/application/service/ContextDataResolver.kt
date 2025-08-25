package com.robotutor.nexora.shared.application.service

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.domain.model.PrincipalData
import com.robotutor.nexora.shared.domain.model.UserData
import reactor.core.publisher.Mono

object ContextDataResolver {
    fun getActorData(): Mono<ActorData> {
        return Mono.deferContextual { context ->
            val actorData = context.getOrEmpty<ActorData>(ActorData::class.java)
            if (actorData.isPresent) {
                createMono(actorData.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0101))
            }
        }
    }

    fun getUserData(): Mono<UserData> {
        return Mono.deferContextual { context ->
            val userData = context.getOrEmpty<UserData>(UserData::class.java)
            if (userData.isPresent) {
                createMono(userData.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0102))
            }
        }
    }

    fun getInvitationData(): Mono<InvitationData> {
        return Mono.deferContextual { context ->
            val invitationData = context.getOrEmpty<InvitationData>(InvitationData::class.java)
            if (invitationData.isPresent) {
                createMono(invitationData.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0103))
            }
        }
    }

    fun getEventPublisher(): Mono<EventPublisher> {
        return Mono.deferContextual {
            createMono(it.get(EventPublisher::class.java))
        }
    }

    fun getPrincipalData(): Mono<PrincipalData> {
        return Mono.deferContextual {
            createMono(it.get(PrincipalData::class.java))
        }
    }
}