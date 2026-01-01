package com.robotutor.nexora.context.device.application.policy

import com.robotutor.nexora.context.device.application.command.RegisterDeviceCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDevicePolicy : Policy<RegisterDeviceCommand> {
    override fun evaluate(input: RegisterDeviceCommand): Mono<PolicyResult> {
        return createMono(PolicyResult.allow())
    }
}