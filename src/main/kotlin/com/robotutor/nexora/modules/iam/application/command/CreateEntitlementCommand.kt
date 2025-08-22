package com.robotutor.nexora.modules.iam.application.command

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId

data class CreateEntitlementCommand(
    val premisesId: PremisesId,
    val roleId: RoleId,
    val resourceId: String,
    val resourceType: ResourceType,
    val action: ActionType
)