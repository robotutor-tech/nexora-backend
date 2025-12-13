package com.robotutor.nexora.context.premises.domain.event

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface PremisesEvent : DomainEvent
sealed interface PremisesDomainEvent : PremisesEvent, DomainEvent

data class PremisesRegisteredEvent(val premisesId: PremisesId, val name: Name, val registeredBy: AccountData) :
    PremisesDomainEvent