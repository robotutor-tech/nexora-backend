package com.robotutor.nexora.shared.domain.vo

data class Resources(
    val premisesId: PremisesId,
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceSelector: ResourceSelector,
    val allowedIds: Set<Identifier> = emptySet(),
    val deniedIds: Set<Identifier> = emptySet()
) : ValueObject

enum class ResourceSelector {
    ALL, SPECIFIC
}