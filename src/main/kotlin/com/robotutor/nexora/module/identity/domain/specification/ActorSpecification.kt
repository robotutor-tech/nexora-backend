package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface ActorSpecification : Specification<Actor>
