package com.robotutor.nexora.module.iam.domain.policy.context

import com.robotutor.nexora.module.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class RotateCredentialPolicyContext(
    val account: AccountAggregate,
    val actorData: ActorData,
)
