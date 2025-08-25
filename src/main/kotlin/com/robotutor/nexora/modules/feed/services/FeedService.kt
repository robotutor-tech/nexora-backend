package com.robotutor.nexora.modules.feed.services

import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class FeedService(
) {
    val logger = Logger(this::class.java)

//    fun createFeed(feedRequest: FeedRequest, premisesActorData: PremisesActorData): Mono<Feed> {
//        return idGeneratorService.generateId(IdType.FEED_ID)
//            .map { feedId -> Feed.from(feedId, feedRequest, premisesActorData) }
//            .flatMap {
//                feedRepository.save(it)
//                    .auditOnSuccess("FEED_CREATED", mapOf("feedId" to it.feedId, "name" to it.name))
//            }
//            .flatMap { feed ->
//                val entitlementResource = EntitlementResource(ResourceType.FEED, feed.feedId)
//                kafkaPublisher.publish("entitlement.create", entitlementResource) { feed }
//            }
//            .logOnSuccess(logger, "Successfully created new feed")
//            .logOnError(logger, "", "Failed to create new feed")
//    }
//
//
//    fun updateFeedValue(feedId: FeedId, feedRequest: FeedValueRequest, actorData: PremisesActorData): Mono<Feed> {
//        return getFeedByFeedId(feedId, actorData)
//            .map { it.updateValue(feedRequest.value) }
//            .flatMap { feedRepository.save(it) }
//            .retryOptimisticLockingFailure()
//            .logOnSuccess(logger, "Successfully updated feed value")
//            .logOnError(logger, "", "Failed to update feed value")
//    }
//
//    fun getFeedByFeedId(feedId: FeedId, premisesActorData: PremisesActorData): Mono<Feed> {
//        return feedRepository.findByFeedIdAndPremisesId(feedId, premisesActorData.premisesId)
//            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0301)) }
//    }
}
