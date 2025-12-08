package com.robotutor.nexora.context.iam.application.seed

import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.vo.Permission

interface PermissionSeedProvider {
    fun getDefaultPermissions(roleType: RoleType): List<Permission>
}