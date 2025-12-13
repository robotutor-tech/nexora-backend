package com.robotutor.nexora.shared.application.service

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
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

//    fun getDeviceData(): Mono<DeviceData> {
//        return Mono.deferContextual { context ->
//            val deviceData = context.getOrEmpty<DeviceData>(DeviceData::class.java)
//            if (deviceData.isPresent) {
//                createMono(deviceData.get())
//            } else {
//                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0104))
//            }
//        }
//    }
//
//    fun getInvitationData(): Mono<InvitationData> {
//        return Mono.deferContextual { context ->
//            val invitationData = context.getOrEmpty<InvitationData>(InvitationData::class.java)
//            if (invitationData.isPresent) {
//                createMono(invitationData.get())
//            } else {
//                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0103))
//            }
//        }
//    }
}