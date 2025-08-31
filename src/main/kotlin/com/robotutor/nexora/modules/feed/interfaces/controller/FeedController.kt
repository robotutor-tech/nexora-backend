package com.robotutor.nexora.modules.feed.interfaces.controller

import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.modules.feed.application.FeedUseCase
import com.robotutor.nexora.modules.feed.interfaces.controller.dto.FeedResponse
import com.robotutor.nexora.modules.feed.interfaces.controller.mapper.FeedMapper
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/feeds")
class FeedController(private val feedUseCase: FeedUseCase) {

//    @RequireAccess(ActionType.CREATE, ResourceType.FEED)
//    @PostMapping
//    fun createFeed(
//        @RequestBody @Validated feedRequest: FeedRequest,
//        premisesActorData: PremisesActorData
//    ): Mono<FeedView> {
//        return feedService.createFeed(feedRequest, premisesActorData).map { FeedView.from(it) }
//    }

    @RequireAccess(ActionType.LIST, ResourceType.FEED)
    @GetMapping
    fun getFeeds(actorData: ActorData, resourcesData: ResourcesData): Flux<FeedResponse> {
        val feedIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.FEED).map { FeedId(it) }
        return feedUseCase.getFeeds(actorData, feedIds).map { FeedMapper.toFeedResponse(it) }
    }

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

