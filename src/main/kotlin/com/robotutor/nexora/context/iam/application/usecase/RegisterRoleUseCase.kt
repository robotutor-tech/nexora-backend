package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.RegisterRoleCommand
import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.repository.RoleRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterRoleUseCase(private val roleRepository: RoleRepository) {
    fun execute(command: RegisterRoleCommand): Mono<RoleAggregate> {
        val roleAggregate = RoleAggregate.register(command.name, command.premisesId, command.type, command.permissions)
        return roleRepository.save(roleAggregate).map { roleAggregate }
    }
}