package com.robotutor.nexora.shared.infrastructure.persistence.document

sealed interface PrincipalDocument
sealed interface ActorPrincipalDocument : PrincipalDocument

data class UserDocument(val userId: String) : ActorPrincipalDocument

data class DeviceDocument(val deviceId: String) : ActorPrincipalDocument

data class InternalDocument(val value: String) : PrincipalDocument

data class InvitationDocument(val invitationId: String) : PrincipalDocument

data class ActorDocument(
    val actorId: String, val roleId: String, val principalDocument: ActorPrincipalDocument
) : PrincipalDocument
