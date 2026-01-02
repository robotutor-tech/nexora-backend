package com.robotutor.nexora.module.iam.application.seed

import com.robotutor.nexora.module.iam.domain.aggregate.RoleType
import com.robotutor.nexora.module.iam.domain.vo.Permission
import com.robotutor.nexora.shared.domain.vo.PremisesId

interface PermissionSeedProvider {
    fun getDefaultPermissions(roleType: RoleType, premisesId: PremisesId): List<Permission>
}