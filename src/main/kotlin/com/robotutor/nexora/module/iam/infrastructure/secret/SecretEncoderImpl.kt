package com.robotutor.nexora.module.iam.infrastructure.secret

import com.robotutor.nexora.module.iam.domain.service.SecretEncoder
import com.robotutor.nexora.module.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.module.iam.domain.vo.HashedCredentialSecret
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SecretEncoderImpl : SecretEncoder {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    override fun encode(secret: CredentialSecret): HashedCredentialSecret {
        return HashedCredentialSecret(passwordEncoder.encode(secret.value))
    }

    override fun matches(
        secret: CredentialSecret,
        hashedSecret: HashedCredentialSecret
    ): Boolean {
        return passwordEncoder.matches(secret.value, hashedSecret.value)
    }
}