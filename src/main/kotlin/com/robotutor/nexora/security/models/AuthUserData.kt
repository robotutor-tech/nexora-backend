package com.robotutor.nexora.security.models

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.gateway.view.ActorType
import com.robotutor.nexora.security.gateway.view.AuthenticationResponseData

interface IAuthenticationData
interface IPremisesActorData

data class AuthUserData(val userId: UserId) : IAuthenticationData, IPremisesActorData {
    companion object {
        fun from(authenticationResponseData: AuthenticationResponseData): AuthUserData {
            return AuthUserData(userId = authenticationResponseData.identifier)
        }
    }
}

data class DeviceData(val deviceId: String) : IAuthenticationData, IPremisesActorData
data class ServerData(val serverId: String) : IAuthenticationData, IPremisesActorData

data class PremisesActorData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val authenticationData: IPremisesActorData
) : IAuthenticationData {
    companion object {
        fun from(authenticationData: AuthenticationResponseData): PremisesActorData {
            return PremisesActorData(
                actorId = authenticationData.identifier,
                premisesId = authenticationData.premisesId!!,
                authenticationData = when (authenticationData.actor!!.type) {
                    ActorType.HUMAN -> AuthUserData(authenticationData.actor.identifier)
                    ActorType.DEVICE -> DeviceData(authenticationData.actor.identifier)
                    ActorType.LOCAL_SERVER -> DeviceData(authenticationData.actor.identifier)
                    ActorType.SERVER -> ServerData(authenticationData.actor.identifier)
                },
            )
        }
    }
}

data class InvitationData(val invitationId: String, val premisesId: PremisesId) : IAuthenticationData {
    companion object {
        fun from(authenticationData: AuthenticationResponseData): InvitationData {
            return InvitationData(
                invitationId = authenticationData.identifier,
                premisesId = authenticationData.premisesId!!
            )
        }
    }
}

typealias UserId = String
typealias ActorId = String
