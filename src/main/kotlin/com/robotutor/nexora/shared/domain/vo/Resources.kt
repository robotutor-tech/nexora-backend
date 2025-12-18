package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.application.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.utility.validation

data class Resources<T : Identifier>(
    val premisesId: PremisesId,
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceSelector: ResourceSelector,
    val allowedIds: Set<T> = emptySet(),
    val deniedIds: Set<T> = emptySet()
) : ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(
            resourceSelector == ResourceSelector.ALL
                    || (resourceSelector == ResourceSelector.SPECIFIC && (allowedIds.isNotEmpty() || deniedIds.isNotEmpty()))
        ) { "At least one resource must be allowed or denied" }
    }
}