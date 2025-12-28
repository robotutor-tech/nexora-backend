package com.robotutor.nexora.shared.domain.vo

data class Resources<T : Identifier>(
    val premisesId: PremisesId,
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceSelector: ResourceSelector,
    val allowedIds: Set<T> = emptySet(),
    val deniedIds: Set<T> = emptySet()
) : ValueObject {
}

enum class ResourceSelector {
    ALL, SPECIFIC
}