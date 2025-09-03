package com.robotutor.nexora.shared.domain.model

interface SequenceId {
    val value: String
}

open class ResourceId(override val value: String) : SequenceId

data class ActorId(override val value: String) : SequenceId

data class DeviceId(override val value: String) : ResourceId(value)

data class Email(override val value: String) : SequenceId

data class FeedId(override val value: String) : ResourceId(value)

data class InvitationId(override val value: String) : SequenceId

data class PremisesId(override val value: String) : ResourceId(value)

data class RoleId(override val value: String) : SequenceId

data class UserId(override val value: String) : SequenceId



data class ZoneId(override val value: String) : ResourceId(value)
