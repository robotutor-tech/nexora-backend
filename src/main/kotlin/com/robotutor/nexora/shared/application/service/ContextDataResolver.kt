package com.robotutor.nexora.shared.application.service

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.DeviceData
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

    fun getAccountData(): Mono<AccountData> {
        return Mono.deferContextual { context ->
            val accountDataOptional = context.getOrEmpty<AccountData>(AccountData::class.java)
            if (accountDataOptional.isPresent) {
                createMono(accountDataOptional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0102))
            }
        }
    }

    fun getDeviceData(): Mono<DeviceData> {
        return Mono.deferContextual { context ->
            val deviceData = context.getOrEmpty<DeviceData>(DeviceData::class.java)
            if (deviceData.isPresent) {
                createMono(deviceData.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0104))
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

    fun getPrincipalData(): Mono<PrincipalData> {
        return Mono.deferContextual {
            createMono(it.get(PrincipalData::class.java))
        }
    }
}