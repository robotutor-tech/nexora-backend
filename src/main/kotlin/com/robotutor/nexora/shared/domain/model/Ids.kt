package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.shared.domain.vo.Identifier


interface SequenceId : Identifier {
    override val value: String
}

@Deprecated("Use ResourceId instead from vo")
open class ResourceId(override val value: String) : SequenceId, Identifier


@Deprecated("Use FeedId instead from vo")
data class FeedId(override val value: String) : ResourceId(value), Identifier

@Deprecated("Use InvitationId instead from vo")
data class InvitationId(override val value: String) : SequenceId
