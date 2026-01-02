package com.robotutor.nexora.module.iam.domain.service

import com.robotutor.nexora.module.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.module.iam.domain.vo.HashedCredentialSecret

interface SecretEncoder {
    fun encode(secret: CredentialSecret): HashedCredentialSecret
    fun matches(secret: CredentialSecret, hashedSecret: HashedCredentialSecret): Boolean
}