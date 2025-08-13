package com.robotutor.nexora.modules.auth.domain.service

import com.robotutor.nexora.modules.auth.domain.model.HashedPassword
import com.robotutor.nexora.modules.auth.domain.model.Password
import org.springframework.security.crypto.password.PasswordEncoder

class PasswordService(private val passwordEncoder: PasswordEncoder) {
    fun encodePassword(password: Password): HashedPassword {
        return HashedPassword(passwordEncoder.encode(password.value))
    }

    fun matches(password: Password, hashedPassword: HashedPassword): Boolean {
        return passwordEncoder.matches(password.value, hashedPassword.value)
    }

}