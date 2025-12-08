package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.RegisterGroupCommand
import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.context.iam.domain.repository.GroupRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterGroupUseCase(
    private val groupRepository: GroupRepository
) {
    fun execute(command: RegisterGroupCommand): Mono<GroupAggregate> {
        val groupAggregate = GroupAggregate.register(command.name, command.premisesId, command.type, command.roleIds)
        return groupRepository.save(groupAggregate).map { groupAggregate }
    }
}