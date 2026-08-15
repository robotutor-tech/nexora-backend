package com.robotutor.nexora.module.identity.infrastructure.secret

import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.vo.*
import com.robotutor.nexora.shared.domain.vo.AccessToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SecretEncoderImpl : SecretEncoder {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    override fun encode(raw: RawSecret): HashedSecret {
        val hashedValue = passwordEncoder.encode(raw.value)
        return when (raw) {
            is RawPassword -> HashedPassword(hashedValue)
            is RawApiSecret -> HashedApiSecret(hashedValue)
        }
    }

    override fun encode(raw: AccessToken): HashAccessToken {
        return HashAccessToken(passwordEncoder.encode(raw.value))
    }

    override fun matches(secret: RawSecret, hashedSecret: HashedSecret): Boolean {
        return passwordEncoder.matches(secret.value, hashedSecret.value)
    }
}
