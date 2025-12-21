package com.robotutor.nexora.context.device.application.policy

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateDevicePolicy(private val deviceRepository: DeviceRepository) : Policy<ActivateDeviceCommand> {
    override fun evaluate(command: ActivateDeviceCommand): Mono<PolicyResult> {
        return deviceRepository.findByDeviceId(command.deviceId)
            .map { PolicyResult.allow() }
    }
}