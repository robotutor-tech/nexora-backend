package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.shared.domain.model.RoleType

data class RoleResponse(
    val roleId: String,
    val premisesId: String,
    val name: String,
    val roleType: RoleType,
)