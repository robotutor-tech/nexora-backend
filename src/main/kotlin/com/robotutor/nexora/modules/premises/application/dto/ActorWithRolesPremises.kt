package com.robotutor.nexora.modules.premises.application.dto

import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
import com.robotutor.nexora.modules.premises.domain.model.Premises

data class ActorWithRolesPremises(
    val actor: ActorWithRoles,
    val premises: Premises
)
