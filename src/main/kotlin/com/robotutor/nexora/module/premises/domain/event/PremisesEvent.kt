package com.robotutor.nexora.module.premises.domain.event

import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface PremisesEvent : Event

data class PremisesRegisteredEvent(val premisesId: PremisesId, val name: Name, val ownerId: AccountId) :
    PremisesEvent