package com.robotutor.nexora.module.feed.application.service

import com.robotutor.nexora.module.feed.application.command.UpdateValueCommand
import com.robotutor.nexora.module.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.module.feed.domain.repository.FeedRepository
import com.robotutor.nexora.module.feed.domain.specification.feedByFeedIdAndPremisesIdSpecification
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UpdateFeedValueService(
    private val feedRepository: FeedRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: UpdateValueCommand): Mono<FeedAggregate> {
        val specification = feedByFeedIdAndPremisesIdSpecification(command.feedId, command.premisesId)
        return feedRepository.findBySpecification(specification)
            .map { feed -> feed.updateValue(command.value) }
            .flatMap { feed -> feedRepository.save(feed) }
            .logOnSuccess(logger, "Successfully get feeds")
            .logOnError(logger, "Failed to get feeds")
    }
}
