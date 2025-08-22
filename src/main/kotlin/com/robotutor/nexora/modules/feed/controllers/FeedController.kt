package com.robotutor.nexora.modules.feed.controllers

import com.robotutor.nexora.modules.feed.services.FeedService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feeds")
class FeedController(private val feedService: FeedService) {

//    @RequireAccess(ActionType.CREATE, ResourceType.FEED)
//    @PostMapping
//    fun createFeed(
//        @RequestBody @Validated feedRequest: FeedRequest,
//        premisesActorData: PremisesActorData
//    ): Mono<FeedView> {
//        return feedService.createFeed(feedRequest, premisesActorData).map { FeedView.from(it) }
//    }
//
//    @RequireAccess(ActionType.LIST, ResourceType.FEED)
//    @GetMapping
//    fun getFeeds(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<FeedView> {
//        val feedIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.FEED)
//        return feedService.getFeeds(premisesActorData, feedIds).map { FeedView.from(it) }
//    }
//
//    @RequireAccess(ActionType.CONTROL, ResourceType.FEED, "feedId")
//    @PatchMapping("/{feedId}/value")
//    fun updateFeedValue(
//        @PathVariable feedId: FeedId,
//        @RequestBody @Validated feedRequest: FeedValueRequest,
//        premisesActorData: PremisesActorData
//    ): Mono<FeedView> {
//        return feedService.updateFeedValue(feedId, feedRequest, premisesActorData).map { FeedView.from(it) }
//    }
//
//    @RequireAccess(ActionType.READ, ResourceType.FEED, "feedId")
//    @GetMapping("/{feedId}")
//    fun getFeed(@PathVariable feedId: FeedId, premisesActorData: PremisesActorData): Mono<FeedView> {
//        return feedService.getFeedByFeedId(feedId, premisesActorData).map { FeedView.from(it) }
//    }
}