package com.robotutor.nexora.modules.premises.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserId

data class PremisesRegisteredEvent(
    val premisesId: PremisesId,
    val name: String,
    val createdBy: UserId,
) : DomainEvent()
