package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.shared.domain.vo.PrincipalData

data class RegisterMachineActorPolicyContext(
    val actorAlreadyExists: Boolean,
    val owner: PrincipalData,
)
