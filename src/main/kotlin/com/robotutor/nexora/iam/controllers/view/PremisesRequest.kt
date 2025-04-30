package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId

data class PremisesRequest(val premisesId: PremisesId)

data class RoleRequest(val name: String, val roleType: RoleType)
