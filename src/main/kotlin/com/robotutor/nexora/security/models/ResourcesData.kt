package com.robotutor.nexora.security.models

import com.robotutor.nexora.security.filters.ResourceEntitlement
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType

data class ResourcesData(val entitlements: List<ResourceEntitlement>) {
    fun getResourceIds(action: ActionType, resourceType: ResourceType): List<String> {
        return entitlements
            .filter { (it.resource.action == action || (action == ActionType.LIST && it.resource.action == ActionType.READ)) && it.resource.type == resourceType }
            .map { it.resource.id }
    }
}