package com.robotutor.nexora.modules.feed.application

import com.robotutor.nexora.modules.feed.application.command.CreateFeedCommand
import com.robotutor.nexora.modules.feed.domain.model.Feed
import com.robotutor.nexora.modules.feed.domain.model.IdType
import com.robotutor.nexora.modules.feed.domain.repository.FeedRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FeedUseCase(
    private val feedRepository: FeedRepository,
    private val idGeneratorService: IdGeneratorService,
) {
    val logger = Logger(this::class.java)

    fun getFeeds(actorData: ActorData, feedIds: List<FeedId>): Flux<Feed> {
        return feedRepository.findAllByPremisesIdAndFeedIdIn(actorData.premisesId, feedIds)
    }

    fun createFeed(createFeedCommand: CreateFeedCommand, actorData: ActorData, zoneId: ZoneId): Mono<Feed> {
        return idGeneratorService.generateId(IdType.FEED_ID, FeedId::class.java)
            .map { feedId -> Feed.create(feedId, zoneId, createFeedCommand, actorData) }
            .flatMap { feed -> feedRepository.save(feed).map { feed } }
            .publishEvents()
            .logOnSuccess(logger, "Successfully created new Feed")
            .logOnError(logger, "", "Failed to create new Feed")
    }
}