package com.robotutor.nexora.feed.services

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.feed.controllers.view.FeedValueRequest
import com.robotutor.nexora.feed.exceptions.NexoraError
import com.robotutor.nexora.feed.models.Feed
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.feed.models.IdType
import com.robotutor.nexora.feed.repositories.FeedRepository
import com.robotutor.nexora.iam.models.Permission
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.utils.retryOptimisticLockingFailure
import com.robotutor.nexora.webClient.exceptions.DataNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class FeedService(private val idGeneratorService: IdGeneratorService, private val feedRepository: FeedRepository) {
    val logger = Logger(this::class.java)

    fun createFeed(feedRequest: FeedRequest, premisesActorData: PremisesActorData): Mono<Feed> {
        return idGeneratorService.generateId(IdType.FEED_ID)
            .map { feedId -> Feed.from(feedId, feedRequest, premisesActorData) }
            .flatMap {
                feedRepository.save(it)
                    .auditOnSuccess("FEED_CREATED", mapOf("feedId" to it.feedId, "name" to it.name))
            }
            .logOnSuccess(logger, "Successfully created new feed")
            .logOnError(logger, "", "Failed to create new feed")
    }

    fun getFeeds(premisesActorData: PremisesActorData): Flux<Feed> {
        val feedIds = premisesActorData.role.policies
            .filter { it.permission == Permission.FEED_READ }
            .map { it.identifier!!.id }
        return feedRepository.findAllByPremisesIdAndFeedIdIn(premisesActorData.premisesId, feedIds)
    }

    fun updateFeedValue(feedId: FeedId, feedRequest: FeedValueRequest, actorData: PremisesActorData): Mono<Feed> {
        return feedRepository.findByFeedIdAndPremisesId(feedId, actorData.premisesId)
            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0301)) }
            .map { it.updateValue(feedRequest.value) }
            .flatMap { feedRepository.save(it) }
            .retryOptimisticLockingFailure()
            .logOnSuccess(logger, "Successfully updated feed value")
            .logOnError(logger, "", "Failed to update feed value")
    }
}
