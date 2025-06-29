package com.robotutor.nexora.security.models

import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.iam.controllers.view.ActorWithRoleView
import com.robotutor.nexora.iam.controllers.view.RoleView
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.filters.ResourceEntitlement
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.gateway.view.AuthenticationResponseData
import com.robotutor.nexora.security.gateway.view.InvitationResponseData
import com.robotutor.nexora.zone.models.ZoneId

interface IAuthenticationData
interface IPremisesActorData

data class AuthUserData(val userId: UserId) : IAuthenticationData, IPremisesActorData {
    companion object {
        fun from(authenticationResponseData: AuthenticationResponseData): AuthUserData {
            return AuthUserData(userId = authenticationResponseData.identifier.id)
        }
    }
}

data class InvitationData(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val name: String,
    val zoneId: ZoneId,
    val invitedBy: ActorId,
) : IAuthenticationData, IPremisesActorData {

    companion object {
        fun from(invitationData: InvitationResponseData): InvitationData {
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
    val role: RoleView,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val entitlements: List<ResourceEntitlement>
) : IAuthenticationData {

    fun getResourceIds(actionType: ActionType, resourceType: ResourceType): List<String> {
        return entitlements
            .filter { it.action == actionType && it.resourceType == resourceType && it.resourceId != "*" }
            .map { it.resourceId }
    }

    companion object {
        fun from(actorData: ActorWithRoleView): PremisesActorData {
            return PremisesActorData(
                actorId = actorData.actorId,
                premisesId = actorData.premisesId,
                role = actorData.role,
                identifier = actorData.identifier,
                entitlements = actorData.entitlement
            )
        }

        fun from(actor: Actor, role: Role): PremisesActorData {
            return PremisesActorData(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                role = RoleView.from(role),
                identifier = actor.identifier,
                entitlements = emptyList()
            )
        }
    }
}

typealias UserId = String
typealias ActorId = String
