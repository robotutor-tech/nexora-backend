package com.robotutor.nexora.context.iam.application.service

import com.robotutor.nexora.context.iam.application.command.RegisterRoleCommand
import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.repository.RoleRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RegisterRoleService(private val roleRepository: RoleRepository) {
    fun execute(command: RegisterRoleCommand): Mono<RoleAggregate> {
        val role = RoleAggregate.register(command.name, command.premisesId, command.type, command.permissions)
        return roleRepository.save(role)
    }

    fun execute(commands: List<RegisterRoleCommand>): Flux<RoleAggregate> {
        val roles = commands.map { command ->
            RoleAggregate.register(command.name, command.premisesId, command.type, command.permissions)
        }
        return roleRepository.saveAll(roles)
    }
}