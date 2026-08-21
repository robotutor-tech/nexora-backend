package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.module.identity.domain.vo.CredentialId

data class RegisterUserAccountPolicyContext(
    val exists: Boolean,
    val credentialId: CredentialId
)
