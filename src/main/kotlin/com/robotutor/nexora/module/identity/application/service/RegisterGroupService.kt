package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.module.identity.application.command.RegisterGroupCommand
import com.robotutor.nexora.module.identity.domain.aggregate.Group
import com.robotutor.nexora.module.identity.domain.repository.GroupRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RegisterGroupService(private val groupRepository: GroupRepository) {
    fun execute(command: RegisterGroupCommand): Mono<Group> {
        val group = Group.register(command.name, command.premisesId, command.type, command.roleIds)
        return groupRepository.save(group)
    }

    fun execute(commands: List<RegisterGroupCommand>): Flux<Group> {
        val groups = commands.map { command ->
            Group.register(command.name, command.premisesId, command.type, command.roleIds)
        }
        return groupRepository.saveAll(groups)
    }
}
