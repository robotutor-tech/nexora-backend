package com.robotutor.nexora.context.iam.config

import com.robotutor.nexora.context.iam.domain.service.SecretService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class Configuration {

    @Bean
    fun passwordService(): SecretService {
        return SecretService(BCryptPasswordEncoder())
    }
}