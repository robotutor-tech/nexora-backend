package com.robotutor.nexora.modules.user.config

import com.robotutor.nexora.modules.user.adapters.outbound.persistance.repository.MongoUserDocumentRepository
import com.robotutor.nexora.modules.user.application.UserUseCase
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("UserConfiguration")
class Configuration {
    @Bean
    fun userUseCase(userRepository: UserRepository, idGeneratorService: IdGeneratorService): UserUseCase {
        return UserUseCase(userRepository = userRepository, idGeneratorService = idGeneratorService)
    }

    @Bean
    fun userRepository(mongoUserDocumentRepository: MongoUserDocumentRepository): UserRepository {
        return mongoUserDocumentRepository
    }
}