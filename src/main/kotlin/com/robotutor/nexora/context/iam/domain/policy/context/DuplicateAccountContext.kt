package com.robotutor.nexora.context.iam.domain.policy.context

import com.robotutor.nexora.context.iam.domain.vo.CredentialId

data class DuplicateAccountContext(
    val accountAlreadyExists: Boolean,
    val credentialId: CredentialId
)
