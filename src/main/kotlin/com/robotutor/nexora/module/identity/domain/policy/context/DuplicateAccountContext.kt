package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.module.identity.domain.vo.CredentialId

data class DuplicateAccountContext(
    val accountAlreadyExists: Boolean,
    val credentialId: CredentialId
)
