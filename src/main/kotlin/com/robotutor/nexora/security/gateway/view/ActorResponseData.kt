package com.robotutor.nexora.security.gateway.view

import com.robotutor.nexora.iam.controllers.view.RoleView
import com.robotutor.nexora.iam.models.ActorState
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier

data class ActorResponseData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val actorIdentifier: Identifier<ActorIdentifier>,
    val role: RoleView,
    val state: ActorState,
)
