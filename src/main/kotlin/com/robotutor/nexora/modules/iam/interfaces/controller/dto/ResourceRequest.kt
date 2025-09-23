package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ResourceRequest(
    @field:NotNull(message = "Action is required")
    val action: ActionType,
    @field:NotNull(message = "Resource type is required")
    val resourceType: ResourceType,
    @field:NotBlank(message = "Resource id is required")
    val resourceId: String,
)