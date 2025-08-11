package com.robotutor.nexora.modules.device.config

import com.robotutor.nexora.modules.device.adapters.outbound.persistance.repository.MongoDeviceRepository
import com.robotutor.nexora.modules.device.application.DeviceUseCase
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("DeviceConfiguration")
class Configuration {
    @Bean
    fun deviceUseCase(
        mongoDeviceRepository: MongoDeviceRepository,
        idGeneratorService: IdGeneratorService
    ): DeviceUseCase {
        return DeviceUseCase(mongoDeviceRepository, idGeneratorService)
    }

}