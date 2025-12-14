package com.robotutor.nexora.shared.domain.model


interface SequenceId {
    val value: String
}

@Deprecated("Use ResourceId instead from vo")
open class ResourceId(override val value: String) : SequenceId

@Deprecated("Use DeviceId instead from vo")
data class DeviceId(override val value: String) : ResourceId(value)

@Deprecated("Use FeedId instead from vo")
data class FeedId(override val value: String) : ResourceId(value)

@Deprecated("Use InvitationId instead from vo")
data class InvitationId(override val value: String) : SequenceId

@Deprecated("Use ZoneId instead from vo")
data class ZoneId(override val value: String) : ResourceId(value)
