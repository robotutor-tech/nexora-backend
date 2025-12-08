package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.aggregate.GroupType
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterGroupCommand(
    val premisesId: PremisesId,
    val name: Name,
    val type: GroupType,
    val roleIds: List<RoleId>
) : Command
