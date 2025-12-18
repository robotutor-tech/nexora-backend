package com.robotutor.nexora.modules.feed.application

import com.robotutor.nexora.modules.feed.application.command.CreateFeedCommand
import com.robotutor.nexora.modules.feed.application.command.FeedValueUpdateCommand
import com.robotutor.nexora.modules.feed.domain.entity.Feed
import com.robotutor.nexora.modules.feed.domain.entity.IdType
import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.modules.feed.domain.exception.NexoraError
import com.robotutor.nexora.modules.feed.domain.repository.FeedRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FeedUseCase(
    private val feedRepository: FeedRepository,
    private val idGeneratorService: IdGeneratorService,
    private val eventPublisher: EventPublisher<FeedEvent>,
    private val resourceCreatedEventPublisher: EventPublisher<ResourceCreatedEvent>,
) {
    val logger = Logger(this::class.java)

    fun getFeeds(actorData: ActorData, feedIds: List<FeedId>): Flux<Feed> {
        return feedRepository.findAllByPremisesIdAndFeedIdIn(actorData.premisesId, feedIds)
    }

    fun createFeed(createFeedCommand: CreateFeedCommand, actorData: ActorData, zoneId: ZoneId): Mono<Feed> {
        return idGeneratorService.generateId(IdType.FEED_ID, FeedId::class.java)
            .map { feedId -> Feed.create(feedId, zoneId, createFeedCommand, actorData) }
            .flatMap { feed ->
                feedRepository.save(feed).map { feed }
                    .publishEvent(
                        resourceCreatedEventPublisher,
                        ResourceCreatedEvent(ResourceType.FEED, ResourceId(feed.feedId.value))
                    )
            }
//            .publishEvents(eventPublisher)
            .logOnSuccess(logger, "Successfully created new Feed")
            .logOnError(logger,  "Failed to create new Feed")
    }

    fun getFeedByFeedId(feedId: FeedId, actorData: ActorData): Mono<Feed> {
        return feedRepository.findByPremisesIdAndFeedId(actorData.premisesId, feedId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0301)))
    }

    fun updateFeedValue(feedValueUpdateCommand: FeedValueUpdateCommand, actorData: ActorData): Mono<Feed> {
        return getFeedByFeedId(feedValueUpdateCommand.feedId, actorData)
            .map { feed -> feed.updateValue(feedValueUpdateCommand.value) }
            .flatMap { feed -> feedRepository.save(feed).map { feed } }
//            .publishEvents(eventPublisher)
    }
}