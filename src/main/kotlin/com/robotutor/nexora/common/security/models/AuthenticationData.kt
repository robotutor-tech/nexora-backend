package com.robotutor.nexora.common.security.models

import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthValidationView
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.DeviceInvitationView
import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.modules.iam.controllers.view.ActorView
import com.robotutor.nexora.modules.iam.models.Actor
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.premises.models.PremisesId

interface IAuthenticationData
interface IPremisesActorData

data class AuthUserData(val userId: UserId) : IAuthenticationData, IPremisesActorData {
    companion object {
        fun from(authValidationView: AuthValidationView): AuthUserData {
            return AuthUserData(userId = authValidationView.identifier.id)
        }
    }
}

data class InvitationData(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val name: String,
    val zoneId: String,
    val invitedBy: ActorId,
) : IAuthenticationData, IPremisesActorData {

    companion object {
        fun from(invitationData: DeviceInvitationView): InvitationData {
            return InvitationData(
                invitationId = invitationData.invitationId,
                premisesId = invitationData.premisesId,
                invitedBy = invitationData.invitedBy,
                name = invitationData.name,
                zoneId = invitationData.zoneId,
            )
        }
    }
}

data class InternalUserData(val userId: String) : IAuthenticationData
data class DeviceData(val deviceId: String) : IAuthenticationData, IPremisesActorData
data class ServerData(val serverId: String) : IAuthenticationData, IPremisesActorData

data class PremisesActorData(
    val actorId: ActorId,
    val role: Role,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
) : IAuthenticationData {

    companion object {
        fun from(actorData: ActorView): PremisesActorData {
            return PremisesActorData(
                actorId = actorData.actorId,
                premisesId = actorData.premisesId,
                role = actorData.role,
                identifier = actorData.identifier,
            )
        }

        fun from(actor: Actor, role: Role): PremisesActorData {
            return PremisesActorData(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                role = role,
                identifier = actor.identifier,
            )
        }
    }
}

typealias UserId = String
typealias ActorId = String
