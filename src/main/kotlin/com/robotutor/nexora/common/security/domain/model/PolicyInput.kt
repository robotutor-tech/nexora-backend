package com.robotutor.nexora.common.security.domain.model

import com.robotutor.nexora.shared.domain.model.ResourceContext
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement

data class PolicyInput(
    val resource: ResourceContext,
    val premisesId: String,
    val entitlements: List<ResourceEntitlement>
)

