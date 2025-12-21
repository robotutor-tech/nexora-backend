package com.robotutor.nexora.context.device.application.policy

import com.robotutor.nexora.context.device.application.command.UpdateMetaDataCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceState
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MetaDataPolicy(private val deviceRepository: DeviceRepository) : Policy<UpdateMetaDataCommand> {
    override fun evaluate(command: UpdateMetaDataCommand): Mono<PolicyResult> {
        return deviceRepository.findByAccountId(command.accountId)
            .map { device ->
                val reasons = mutableListOf<String>()
                if (device.getState() != DeviceState.REGISTERED) {
                    reasons.add("Device is not in REGISTERED state")
                }
                if (reasons.isEmpty()) PolicyResult.allow() else PolicyResult.deny(reasons)
            }
            .switchIfEmpty(createMono(PolicyResult.deny(listOf("Device not found"))))
    }
}