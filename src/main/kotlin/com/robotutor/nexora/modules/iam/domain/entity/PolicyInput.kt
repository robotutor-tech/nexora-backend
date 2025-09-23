package com.robotutor.nexora.modules.iam.domain.entity

import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceContext
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement

data class PolicyInput(
    val resource: ResourceContext,
    val premisesId: PremisesId,
    val entitlements: List<ResourceEntitlement>
)