package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.aggregate.RoleType
import com.robotutor.nexora.module.identity.domain.vo.Permission
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterRoleCommand(
    val premisesId: PremisesId,
    val name: Name,
    val type: RoleType,
    val permissions: List<Permission>
) : Command
