package com.robotutor.nexora.context.feed.application.usecase

import com.robotutor.nexora.context.feed.application.command.GetFeedsQuery
import com.robotutor.nexora.context.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.feed.domain.repository.FeedRepository
import com.robotutor.nexora.context.feed.domain.specification.FeedByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.specification.AuthorizedQueryBuilder
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class FeedUseCase(
    private val feedRepository: FeedRepository,
    private val authorizedQueryBuilder: AuthorizedQueryBuilder<FeedId, FeedAggregate>,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    fun execute(query: GetFeedsQuery): Flux<FeedAggregate> {
        val specification = authorizedQueryBuilder.build(query.resources)
            .and(FeedByPremisesIdSpecification(query.resources.premisesId))
        return feedRepository.findAll(specification)
            .logOnSuccess(logger, "Successfully get feeds")
            .logOnError(logger, "Failed to get feeds")
    }
}
