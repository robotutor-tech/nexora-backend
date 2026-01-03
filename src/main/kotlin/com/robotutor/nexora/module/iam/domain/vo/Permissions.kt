package com.robotutor.nexora.module.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.Resources

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
        return permissions.any { it.isEqual(resource) }
    }

    fun getResources(action: ActionType, type: ResourceType): Resources {
        val resourceSelector = getResourceSelector(action, type)
        val allowedIds = if (resourceSelector == ResourceSelector.ALL) emptySet() else getAllowedIds(action, type)
        val deniedIds = override
            .filter {
                it.permission.isEqual(action, type) && it.effect == PermissionEffect.DENY
            }
            .map { it.permission.resourceId }.toSet()
        return Resources(
            premisesId = premisesId,
            resourceType = type,
            actionType = action,
            resourceSelector = resourceSelector,
            allowedIds = allowedIds,
            deniedIds = deniedIds
        )
    }

    private fun getResourceSelector(action: ActionType, type: ResourceType): ResourceSelector {
        return if (permissions.any { it.isEqual(action, type) && it.resourceId == ResourceId.ALL }) {
            ResourceSelector.ALL
        } else {
            ResourceSelector.SPECIFIC
        }
    }

    private fun getAllowedIds(
        action: ActionType,
        type: ResourceType,
    ): Set<ResourceId> {
        val overridesAllowedIds = override
            .filter { it.permission.isEqual(action, type) && it.effect == PermissionEffect.ALLOW }
            .map { it.permission.resourceId }
        return permissions.filter { it.isEqual(action, type) }
            .map { it.resourceId }
            .plus(overridesAllowedIds)
            .toSet()
    }
}
