package com.robotutor.nexora.shared.infrastructure.persistence.mapper

import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.infrastructure.persistence.document.*

object PrincipalDocumentMapper {
    fun toPrincipalContext(principalDocument: PrincipalDocument): PrincipalContext {
        return when (principalDocument) {
            is ActorDocument -> ActorContext(
                actorId = ActorId(principalDocument.actorId),
                roleId = RoleId(principalDocument.roleId),
                principalContext = toActorPrincipalContext(principalDocument.principalDocument)
            )

            is DeviceDocument -> DeviceContext(DeviceId(principalDocument.deviceId))
            is UserDocument -> UserContext(UserId(principalDocument.userId))
            is InternalDocument -> InternalContext(principalDocument.value)
            is InvitationDocument -> InvitationContext(InvitationId(principalDocument.invitationId))
        }
    }


    fun toActorPrincipalContext(principalDocument: ActorPrincipalDocument): ActorPrincipalContext {
        return when (principalDocument) {
            is DeviceDocument -> DeviceContext(DeviceId(principalDocument.deviceId))
            is UserDocument -> UserContext(UserId(principalDocument.userId))
        }
    }

    fun toPrincipalDocument(principal: PrincipalContext): PrincipalDocument {
        return when (principal) {
            is ActorContext -> ActorDocument(
                actorId = principal.actorId.value,
                roleId = principal.roleId.value,
                principalDocument = toActorPrincipalDocument(principal.principalContext)
            )

            is DeviceContext -> DeviceDocument(
                deviceId = principal.deviceId.value
            )

            is UserContext -> UserDocument(principal.userId.value)
            is InternalContext -> InternalDocument(principal.value)
            is InvitationContext -> InvitationDocument(principal.invitationId.value)
        }
    }

    fun toActorPrincipalDocument(principal: ActorPrincipalContext): ActorPrincipalDocument {
        return when (principal) {
            is DeviceContext -> DeviceDocument(principal.deviceId.value)
            is UserContext -> UserDocument(principal.userId.value)
        }
    }
}