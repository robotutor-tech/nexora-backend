package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.shared.domain.utility.validation


interface SequenceId {
    val value: String
}

open class ResourceId(override val value: String) : SequenceId

data class ActorId(override val value: String) : SequenceId

data class DeviceId(override val value: String) : ResourceId(value)

data class Email(override val value: String) : SequenceId {
    init {
        val regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"
        validation(Regex(regex, RegexOption.IGNORE_CASE).matches(value)) { "Email must be valid" }
    }
}

data class FeedId(override val value: String) : ResourceId(value)

data class InvitationId(override val value: String) : SequenceId

@Deprecated("Use PremisesId instead from vo")
data class PremisesId(override val value: String) : ResourceId(value)

data class RoleId(override val value: String) : SequenceId

data class ZoneId(override val value: String) : ResourceId(value)
