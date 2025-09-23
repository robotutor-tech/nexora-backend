package com.robotutor.nexora.shared.domain.model

data class ResourcesData(val entitlements: List<ResourceEntitlement>) {
    fun getResourceIds(action: ActionType, resourceType: ResourceType): List<String> {
        return entitlements
            .filter { (it.resource.action == action || (action == ActionType.LIST && it.resource.action == ActionType.READ)) && it.resource.type == resourceType }
            .map { it.resource.id }
    }
}

data class ResourceContext(val type: ResourceType, val id: String, val action: ActionType)

data class ResourceEntitlement(
    val resource: ResourceContext, val premisesId: PremisesId
)
