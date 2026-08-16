package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.module.identity.domain.vo.HashedSecret
import com.robotutor.nexora.module.identity.domain.vo.RawSecret

data class MatchPasswordPolicyContext(
    val rawPassword: RawSecret,
    val hashedPassword: HashedSecret,
)
