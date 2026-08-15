package com.robotutor.nexora.module.identity.domain.service

import com.robotutor.nexora.module.identity.domain.vo.HashAccessToken
import com.robotutor.nexora.module.identity.domain.vo.HashedSecret
import com.robotutor.nexora.module.identity.domain.vo.RawSecret
import com.robotutor.nexora.shared.domain.vo.AccessToken

interface SecretEncoder {
    fun encode(raw: RawSecret): HashedSecret
    fun encode(raw: AccessToken): HashAccessToken
    fun matches(secret: RawSecret, hashedSecret: HashedSecret): Boolean
}
