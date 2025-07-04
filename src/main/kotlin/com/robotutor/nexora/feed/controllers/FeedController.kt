package com.robotutor.nexora.feed.controllers

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.feed.controllers.view.FeedValueRequest
import com.robotutor.nexora.feed.controllers.view.FeedView
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.feed.services.FeedService
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.RequireAccess
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.ResourcesData
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

    @RequireAccess(ActionType.CREATE, ResourceType.FEED)
    @PostMapping
    fun createFeed(
        @RequestBody @Validated feedRequest: FeedRequest,
        premisesActorData: PremisesActorData
    ): Mono<FeedView> {
        return feedService.createFeed(feedRequest, premisesActorData).map { FeedView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.FEED)
    @GetMapping
    fun getFeeds(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<FeedView> {
        val feedIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.FEED)
        return feedService.getFeeds(premisesActorData, feedIds).map { FeedView.from(it) }
    }

    @RequireAccess(ActionType.CONTROL, ResourceType.FEED, "feedId")
    @PatchMapping("/{feedId}/value")
    fun updateFeedValue(
        @PathVariable feedId: FeedId,
        @RequestBody @Validated feedRequest: FeedValueRequest,
        premisesActorData: PremisesActorData
    ): Mono<FeedView> {
        return feedService.updateFeedValue(feedId, feedRequest, premisesActorData).map { FeedView.from(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.FEED, "feedId")
    @GetMapping("/{feedId}")
    fun getFeed(@PathVariable feedId: FeedId, premisesActorData: PremisesActorData): Mono<FeedView> {
        return feedService.getFeedByFeedId(feedId, premisesActorData).map { FeedView.from(it) }
    }
}