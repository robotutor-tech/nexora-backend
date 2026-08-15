package com.robotutor.nexora.module.identity.domain.policy.context

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.shared.domain.vo.SubjectId

data class RegisterAccountPolicyContext(
    val accounts: List<Account>,
    val credentialId: CredentialId,
    val subjectId: SubjectId,
)
