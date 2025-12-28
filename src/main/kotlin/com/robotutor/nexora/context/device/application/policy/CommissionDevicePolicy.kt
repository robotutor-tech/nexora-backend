package com.robotutor.nexora.context.device.application.policy

import com.robotutor.nexora.context.device.application.command.CommissionDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceState
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.specification.DeviceByDeviceIdSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommissionDevicePolicy(private val deviceRepository: DeviceRepository) : Policy<CommissionDeviceCommand> {
    override fun evaluate(command: CommissionDeviceCommand): Mono<PolicyResult> {
        val specification = DeviceByPremisesIdSpecification(command.actorData.premisesId)
            .and(DeviceByDeviceIdSpecification(command.deviceId))
        return deviceRepository.findBySpecification(specification)
            .map { device ->
                val reasons = mutableListOf<String>()
                if (device.getState() != DeviceState.REGISTERED) {
                    reasons.add("Device is not in commission state")
                }
                if (reasons.isEmpty()) PolicyResult.allow() else PolicyResult.deny(reasons)
            }
            .switchIfEmpty(createMono(PolicyResult.deny(listOf("Device not found"))))
    }
}