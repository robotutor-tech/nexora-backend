package com.robotutor.nexora.common.security.models

import com.robotutor.nexora.modules.auth.interfaces.controller.dto.DeviceInvitationView
import com.robotutor.nexora.modules.iam.adapters.model.RoleDocument
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.Identifier

interface IAuthenticationData
interface IPremisesActorData

data class AuthUserData(val userId: UserId) : IAuthenticationData, IPremisesActorData {
//    companion object {
//        fun from(authValidationView: AuthValidationView): AuthUserData {
//            return AuthUserData(userId = authValidationView.identifier.id)
//        }
//    }
}

data class InvitationData(
    val invitationId: String,
    val premisesId: String,
    val name: String,
    val zoneId: String,
    val invitedBy: String,
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
    val roleDocument: RoleDocument,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorPrincipalType>,
) : IAuthenticationData {

//    companion object {
//        fun from(actorData: ActorView): PremisesActorData {
//            return PremisesActorData(
//                actorId = actorData.actorId,
//                premisesId = actorData.premisesId,
//                roleDocument = actorData.roleDocument,
//                identifier = actorData.identifier,
//            )
//        }
//
//        fun from(actorDocument: ActorDocument, roleDocument: RoleDocument): PremisesActorData {
//            return PremisesActorData(
//                actorId = actorDocument.actorId,
//                premisesId = actorDocument.premisesId,
//                roleDocument = roleDocument,
//                identifier = actorDocument.identifier,
//            )
//        }
//    }
}

typealias UserId = String
typealias PremisesId = String
typealias ActorId = String
