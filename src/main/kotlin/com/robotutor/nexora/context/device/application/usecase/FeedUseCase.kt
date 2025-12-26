package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.GetFeedsQuery
import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.device.domain.repository.FeedRepository
import com.robotutor.nexora.context.device.domain.specification.FeedByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.specification.AuthorizedQueryBuilder
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class FeedUseCase(
    private val feedRepository: FeedRepository,
    private val authorizedQueryBuilder: AuthorizedQueryBuilder<FeedId, FeedAggregate>,
) {
    private val logger = Logger(this::class.java)

    fun execute(query: GetFeedsQuery): Flux<FeedAggregate> {
        val specification = authorizedQueryBuilder.build(query.resources)
            .and(FeedByPremisesIdSpecification(query.resources.premisesId))
        return feedRepository.findAll(specification)
            .logOnSuccess(logger, "Successfully get feeds")
            .logOnError(logger, "Failed to get feeds")
    }
}