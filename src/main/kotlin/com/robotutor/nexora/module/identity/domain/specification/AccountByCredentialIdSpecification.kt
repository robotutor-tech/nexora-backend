package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.vo.CredentialId

class AccountByCredentialIdSpecification(val credentialId: CredentialId) : AccountSpecification {
    override fun isSatisfiedBy(candidate: Account): Boolean {
        return candidate.credential.credentialId == credentialId
    }
}
