package com.robotutor.nexora.context.iam.domain.service

import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.domain.vo.HashedCredentialSecret
import org.springframework.security.crypto.password.PasswordEncoder

class SecretService(private val passwordEncoder: PasswordEncoder) {
    fun encode(secret: CredentialSecret): HashedCredentialSecret {
        return HashedCredentialSecret(passwordEncoder.encode(secret.value))
    }

    fun matches(secret: CredentialSecret, hashedSecret: HashedCredentialSecret): Boolean {
        return passwordEncoder.matches(secret.value, hashedSecret.value)
    }
}