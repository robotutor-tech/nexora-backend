package com.robotutor.nexora.context.user.application.policy

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.context.user.application.command.RegisterUserCommand
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RegisterUserPolicy(private val userRepository: UserRepository) : Policy<RegisterUserCommand> {
    override fun evaluate(command: RegisterUserCommand): Mono<PolicyResult> {
        return userRepository.findByEmail(command.email)
            .map {
                val reasons = listOf("User with email ${command.email.value} already exists")
                PolicyResult.deny(reasons)
            }
            .switchIfEmpty(createMono(PolicyResult.allow()))
    }

    override fun getName(): String {
        return "UserRegistrationPolicy"
    }
}