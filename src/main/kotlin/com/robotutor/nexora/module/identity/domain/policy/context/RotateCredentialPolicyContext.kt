package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class RotateCredentialPolicyContext(
    val account: AccountAggregate,
    val actorData: ActorData,
)
