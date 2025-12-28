package com.robotutor.nexora.common.security.interfaces.view

import com.robotutor.nexora.shared.domain.vo.*

data class AuthorizedResources(
    val premisesId: String,
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceSelector: ResourceSelector,
    val allowedIds: Set<String>,
    val deniedIds: Set<String>
) {
    fun <T : Identifier> toResources(clazz: Class<T>): Resources<T> {
        return Resources(
            premisesId = PremisesId(premisesId),
            resourceType = resourceType,
            actionType = actionType,
            resourceSelector = resourceSelector,
            allowedIds = allowedIds.map { clazz.getDeclaredConstructor(String::class.java).newInstance(it) }.toSet(),
            deniedIds = deniedIds.map { clazz.getDeclaredConstructor(String::class.java).newInstance(it) }.toSet()
        )
    }
}
