package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.vo.Permission
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterRoleCommand(
    val premisesId: PremisesId,
    val name: Name,
    val type: RoleType,
    val permissions: List<Permission>
) : Command
