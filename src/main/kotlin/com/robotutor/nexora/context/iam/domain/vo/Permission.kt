package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class Permission(
    val action: ActionType,
    val resourceType: ResourceType,
    val resource: ResourceId,
    val premisesId: PremisesId
) {
    fun isEqual(resource: Resource): Boolean {
        return premisesId == resource.premisesId &&
                action == resource.action &&
                resourceType == resource.type &&
                isResourceEqual(resource.resourceId)
    }

    private fun isResourceEqual(other: ResourceId): Boolean {
        return resource == ResourceId.ALL || resource == other
    }
}
