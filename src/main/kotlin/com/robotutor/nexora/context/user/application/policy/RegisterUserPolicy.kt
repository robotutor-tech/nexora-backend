package com.robotutor.nexora.context.user.application.policy

import com.robotutor.nexora.context.user.application.command.RegisterUserCommand
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RegisterUserPolicy(private val userRepository: UserRepository) : Policy<RegisterUserCommand> {
    override fun evaluate(input: RegisterUserCommand): Mono<PolicyResult> {
        return userRepository.findByEmail(input.email)
            .hasElement()
            .map {
                if (it) PolicyResult.deny(listOf("User with email ${input.email.value} already exists"))
                else PolicyResult.allow()
            }
    }
}