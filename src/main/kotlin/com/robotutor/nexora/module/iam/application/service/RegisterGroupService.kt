package com.robotutor.nexora.module.iam.application.service

import com.robotutor.nexora.module.iam.application.command.RegisterGroupCommand
import com.robotutor.nexora.module.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.module.iam.domain.repository.GroupRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RegisterGroupService(private val groupRepository: GroupRepository) {
    fun execute(command: RegisterGroupCommand): Mono<GroupAggregate> {
        val groupAggregate = GroupAggregate.register(command.name, command.premisesId, command.type, command.roleIds)
        return groupRepository.save(groupAggregate)
    }

    fun execute(commands: List<RegisterGroupCommand>): Flux<GroupAggregate> {
        val groupAggregates = commands.map { command ->
            GroupAggregate.register(command.name, command.premisesId, command.type, command.roleIds)
        }
        return groupRepository.saveAll(groupAggregates)
    }
}