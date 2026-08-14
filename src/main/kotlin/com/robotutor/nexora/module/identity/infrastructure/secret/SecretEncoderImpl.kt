package com.robotutor.nexora.module.identity.infrastructure.secret

import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.vo.CredentialSecret
import com.robotutor.nexora.module.identity.domain.vo.HashAccessToken
import com.robotutor.nexora.module.identity.domain.vo.HashedCredentialSecret
import com.robotutor.nexora.shared.domain.vo.AccessToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SecretEncoderImpl : SecretEncoder {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    override fun encode(secret: CredentialSecret): HashedCredentialSecret {
        return HashedCredentialSecret(passwordEncoder.encode(secret.value))
    }

    override fun encode(raw: AccessToken): HashAccessToken {
        return HashAccessToken(passwordEncoder.encode(raw.value))
    }

    override fun matches(secret: CredentialSecret, hashedSecret: HashedCredentialSecret): Boolean {
        return passwordEncoder.matches(secret.value, hashedSecret.value)
    }
}
