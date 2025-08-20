package com.robotutor.nexora.modules.auth.config

import com.robotutor.nexora.modules.auth.application.factory.TokenFactory
import com.robotutor.nexora.modules.auth.domain.service.PasswordService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class Configuration {

    @Bean
    fun passwordService(): PasswordService {
        return PasswordService(BCryptPasswordEncoder())
    }

    @Bean
    fun tokenFactory(): TokenFactory {
        return TokenFactory()
    }
}