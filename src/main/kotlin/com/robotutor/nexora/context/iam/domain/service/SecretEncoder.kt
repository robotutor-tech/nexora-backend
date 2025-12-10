package com.robotutor.nexora.context.iam.domain.service

import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.domain.vo.HashedCredentialSecret

interface SecretEncoder {
    fun encode(secret: CredentialSecret): HashedCredentialSecret
    fun matches(secret: CredentialSecret, hashedSecret: HashedCredentialSecret): Boolean
}