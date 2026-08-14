package com.robotutor.nexora.module.identity.application.seed

import com.robotutor.nexora.module.identity.domain.aggregate.RoleType
import com.robotutor.nexora.module.identity.domain.vo.Permission
import com.robotutor.nexora.shared.domain.vo.PremisesId

interface PermissionSeedProvider {
    fun getDefaultPermissions(roleType: RoleType, premisesId: PremisesId): List<Permission>
}
