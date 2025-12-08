package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.GetActorsQuery
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class GetActorsUseCase(
    private val actorRepository: ActorRepository,
) {
    fun execute(query: GetActorsQuery): Flux<ActorAggregate> {
        return actorRepository.findAllByAccountId(query.accountId)
    }
}