package com.robotutor.nexora.context.iam.domain.policy.context

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class RotateCredentialPolicyContext(
    val account: AccountAggregate,
    val actorData: ActorData,
)
