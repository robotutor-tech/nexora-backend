package com.robotutor.nexora.shared.config

import com.robotutor.nexora.shared.adapters.persistance.service.MongoIdGeneratorServiceService
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("SharedConfiguration")
class Configuration {
    @Bean
    fun idGeneratorService(mongoIdGeneratorService: MongoIdGeneratorServiceService): IdGeneratorService {
        return mongoIdGeneratorService
    }
}