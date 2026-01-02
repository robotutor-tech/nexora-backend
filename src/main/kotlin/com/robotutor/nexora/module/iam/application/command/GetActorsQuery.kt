package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class GetActorsQuery(val accountId: AccountId)
data class GetActorQuery(val actorId: ActorId, val premisesId: PremisesId)
