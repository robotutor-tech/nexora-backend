package com.robotutor.nexora.common.security.gateway.view

import com.robotutor.nexora.modules.auth.gateways.view.RoleView
import com.robotutor.nexora.modules.iam.models.ActorState
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier

data class ActorResponseData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val role: RoleView,
    val state: ActorState,
)
