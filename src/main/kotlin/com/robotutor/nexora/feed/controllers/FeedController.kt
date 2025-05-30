package com.robotutor.nexora.feed.controllers

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.feed.controllers.view.FeedValueRequest
import com.robotutor.nexora.feed.controllers.view.FeedView
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.feed.services.FeedService
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/feeds")
class FeedController(private val feedService: FeedService) {

    @PostMapping
    fun createFeed(
        @RequestBody @Validated feedRequest: FeedRequest,
        premisesActorData: PremisesActorData
    ): Mono<FeedView> {
        return feedService.createFeed(feedRequest, premisesActorData).map { FeedView.from(it) }
    }

    @GetMapping
    fun getFeeds(premisesActorData: PremisesActorData): Flux<FeedView> {
        return feedService.getFeeds(premisesActorData).map { FeedView.from(it) }
    }

    @PatchMapping("/{feedId}/value")
    fun updateFeedValue(
        @PathVariable feedId: FeedId,
        @RequestBody @Validated feedRequest: FeedValueRequest,
        premisesActorData: PremisesActorData
    ): Mono<FeedView> {
        return feedService.updateFeedValue(feedId, feedRequest, premisesActorData).map { FeedView.from(it) }
    }
}