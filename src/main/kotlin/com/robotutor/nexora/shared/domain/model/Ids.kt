package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.shared.domain.utility.validation


interface SequenceId {
    val value: String
}

@Deprecated("Use ResourceId instead from vo")
open class ResourceId(override val value: String) : SequenceId

@Deprecated("Use ActorId instead from vo")
data class ActorId(override val value: String) : SequenceId

@Deprecated("Use DeviceId instead from vo")
data class DeviceId(override val value: String) : ResourceId(value)

@Deprecated("Use GroupId instead from vo")
data class Email(override val value: String) : SequenceId {
    init {
        val regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"
        validation(Regex(regex, RegexOption.IGNORE_CASE).matches(value)) { "Email must be valid" }
    }
}

@Deprecated("Use FeedId instead from vo")
data class FeedId(override val value: String) : ResourceId(value)

@Deprecated("Use InvitationId instead from vo")
data class InvitationId(override val value: String) : SequenceId

@Deprecated("Use PremisesId instead from vo")
data class PremisesId(override val value: String) : ResourceId(value)

@Deprecated("Use RoleId instead from vo")
data class RoleId(override val value: String) : SequenceId

@Deprecated("Use ZoneId instead from vo")
data class ZoneId(override val value: String) : ResourceId(value)
