package com.robotutor.nexora.modules.premises.domain.event

import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId

data class PremisesCreatedEvent(val premisesId: PremisesId, val name: Name) : PremisesEvent
