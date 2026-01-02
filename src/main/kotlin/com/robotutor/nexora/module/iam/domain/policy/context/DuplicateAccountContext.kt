package com.robotutor.nexora.module.iam.domain.policy.context

import com.robotutor.nexora.module.iam.domain.vo.CredentialId

data class DuplicateAccountContext(
    val accountAlreadyExists: Boolean,
    val credentialId: CredentialId
)
