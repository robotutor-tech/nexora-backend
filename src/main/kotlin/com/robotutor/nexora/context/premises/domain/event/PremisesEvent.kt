package com.robotutor.nexora.context.premises.domain.event

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.model.PremisesId

sealed interface PremisesEvent : DomainEvent

data class PremisesRegisteredEvent(val premisesId: PremisesId, val name: Name, val registeredBy: AccountData) :
    PremisesEvent