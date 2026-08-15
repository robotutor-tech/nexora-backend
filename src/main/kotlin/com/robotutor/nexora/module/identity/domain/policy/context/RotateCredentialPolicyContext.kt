package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.shared.domain.vo.ActorData

data class RotateCredentialPolicyContext(
    val account: Account,
    val actorData: ActorData,
)
