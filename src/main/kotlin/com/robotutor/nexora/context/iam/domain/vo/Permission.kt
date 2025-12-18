package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class Permission(
    val action: ActionType,
    val resourceType: ResourceType,
    val resourceId: ResourceId,
    val premisesId: PremisesId
) {


    fun isEqual(resource: Resource): Boolean {
        return premisesId == resource.premisesId &&
                action == resource.action &&
                resourceType == resource.type &&
                isResourceEqual(resource.resourceId)
    }

    fun isEqual(actionType: ActionType, resourceType: ResourceType): Boolean {
        return action == actionType && this.resourceType == resourceType
    }

    private fun isResourceEqual(other: ResourceId): Boolean {
        return resourceId == ResourceId.ALL || resourceId == other
    }
}
