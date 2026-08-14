package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.shared.domain.vo.principal.AccountData

data class RegisterMachineActorPolicyContext(
    val actorAlreadyExists: Boolean,
    val owner: AccountData,
)
