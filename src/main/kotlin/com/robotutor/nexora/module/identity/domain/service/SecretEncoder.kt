package com.robotutor.nexora.module.identity.domain.service

import com.robotutor.nexora.module.identity.domain.vo.CredentialSecret
import com.robotutor.nexora.module.identity.domain.vo.HashAccessToken
import com.robotutor.nexora.module.identity.domain.vo.HashedCredentialSecret
import com.robotutor.nexora.shared.domain.vo.AccessToken

interface SecretEncoder {
    fun encode(secret: CredentialSecret): HashedCredentialSecret
    fun encode(raw: AccessToken): HashAccessToken
    fun matches(secret: CredentialSecret, hashedSecret: HashedCredentialSecret): Boolean
}
