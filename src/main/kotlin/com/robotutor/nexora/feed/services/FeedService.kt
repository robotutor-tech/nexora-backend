package com.robotutor.nexora.feed.services

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.feed.models.Feed
import com.robotutor.nexora.feed.models.IdType
import com.robotutor.nexora.feed.repositories.FeedRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedService(private val idGeneratorService: IdGeneratorService, private val feedRepository: FeedRepository) {
    val logger = Logger(this::class.java)

    fun createFeed(feedRequest: FeedRequest, premisesActorData: PremisesActorData): Mono<Feed> {
        return idGeneratorService.generateId(IdType.FEED_ID)
            .flatMap { feedId ->
                feedRepository.save(Feed.from(feedId, feedRequest, premisesActorData))
            }
            .logOnSuccess(logger, "Successfully created new feed")
            .logOnError(logger, "", "Failed to create new feed")
    }

}
