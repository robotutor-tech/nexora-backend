package com.robotutor.nexora.modules.iam.application.command

import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleType

data class CreateRoleCommand(val premisesId: PremisesId, val name: Name, val roleType: RoleType)