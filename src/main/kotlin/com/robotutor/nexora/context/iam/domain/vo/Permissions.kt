package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.PremisesId

data class Permissions(
    val premisesId: PremisesId,
    val permissions: Set<Permission>,
    val override: Set<PermissionOverride>
) {
    fun authorize(resource: Resource): Boolean {
        val overridePermission = override.find { it.permission.isEqual(resource) }
        if (overridePermission != null) {
            return overridePermission.effect == PermissionEffect.ALLOW
        }
        return permissions.find { it.isEqual(resource) } != null
    }
}
