package com.robotutor.nexora.modules.feed.services

import com.robotutor.nexora.modules.feed.controllers.view.FeedRequest
import com.robotutor.nexora.modules.feed.controllers.view.FeedValueRequest
import com.robotutor.nexora.modules.feed.exceptions.NexoraError
import com.robotutor.nexora.modules.feed.models.Feed
import com.robotutor.nexora.modules.feed.models.FeedId
import com.robotutor.nexora.modules.feed.models.IdType
import com.robotutor.nexora.modules.feed.repositories.FeedRepository
import com.robotutor.nexora.modules.iam.services.EntitlementResource
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.services.IdGeneratorService
import com.robotutor.nexora.utils.retryOptimisticLockingFailure
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.DataNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class FeedService(
    private val idGeneratorService: IdGeneratorService,
    private val feedRepository: FeedRepository,
    private val kafkaPublisher: KafkaPublisher
) {
    val logger = Logger(this::class.java)

    fun createFeed(feedRequest: FeedRequest, premisesActorData: PremisesActorData): Mono<Feed> {
        return idGeneratorService.generateId(IdType.FEED_ID)
            .map { feedId -> Feed.from(feedId, feedRequest, premisesActorData) }
            .flatMap {
                feedRepository.save(it)
                    .auditOnSuccess("FEED_CREATED", mapOf("feedId" to it.feedId, "name" to it.name))
            }
            .flatMap { feed ->
                val entitlementResource = EntitlementResource(ResourceType.FEED, feed.feedId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { feed }
            }
            .logOnSuccess(logger, "Successfully created new feed")
            .logOnError(logger, "", "Failed to create new feed")
    }

    fun getFeeds(premisesActorData: PremisesActorData, feedIds: List<FeedId>): Flux<Feed> {
        return feedRepository.findAllByPremisesIdAndFeedIdIn(premisesActorData.premisesId, feedIds)
    }

    fun updateFeedValue(feedId: FeedId, feedRequest: FeedValueRequest, actorData: PremisesActorData): Mono<Feed> {
        return getFeedByFeedId(feedId, actorData)
            .map { it.updateValue(feedRequest.value) }
            .flatMap { feedRepository.save(it) }
            .retryOptimisticLockingFailure()
            .logOnSuccess(logger, "Successfully updated feed value")
            .logOnError(logger, "", "Failed to update feed value")
    }

    fun getFeedByFeedId(feedId: FeedId, premisesActorData: PremisesActorData): Mono<Feed> {
        return feedRepository.findByFeedIdAndPremisesId(feedId, premisesActorData.premisesId)
            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0301)) }
    }
}
