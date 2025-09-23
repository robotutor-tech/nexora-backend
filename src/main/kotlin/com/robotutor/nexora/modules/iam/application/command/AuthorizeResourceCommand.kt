package com.robotutor.nexora.modules.iam.application.command

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType

data class AuthorizeResourceCommand(
    val resourceType: ResourceType,
    val action: ActionType,
    val resourceId: String
)