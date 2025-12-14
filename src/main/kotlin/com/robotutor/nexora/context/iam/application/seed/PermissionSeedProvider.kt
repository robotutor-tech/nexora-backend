package com.robotutor.nexora.context.iam.application.seed

import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.vo.Permission
import com.robotutor.nexora.shared.domain.vo.PremisesId

interface PermissionSeedProvider {
    fun getDefaultPermissions(roleType: RoleType, premisesId: PremisesId): List<Permission>
}