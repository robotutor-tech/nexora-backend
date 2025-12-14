package com.robotutor.nexora.modules.feed.interfaces.controller

import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.modules.feed.application.FeedUseCase
import com.robotutor.nexora.modules.feed.interfaces.controller.dto.FeedResponse
import com.robotutor.nexora.modules.feed.interfaces.controller.dto.FeedValueRequest
import com.robotutor.nexora.modules.feed.interfaces.controller.mapper.FeedMapper
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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

    @RequireAccess(ActionType.READ, ResourceType.FEED)
    @GetMapping
    fun getFeeds(actorData: ActorData, resourcesData: ResourcesData): Flux<FeedResponse> {
        val feedIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.FEED).map { FeedId(it) }
        return feedUseCase.getFeeds(actorData, feedIds).map { FeedMapper.toFeedResponse(it) }
    }

    @RequireAccess(ActionType.CONTROL, ResourceType.FEED, "feedId")
    @PatchMapping("/{feedId}/value")
    fun updateFeedValue(
        @PathVariable feedId: String,
        @RequestBody @Validated feedRequest: FeedValueRequest,
        actorData: ActorData
    ): Mono<FeedResponse> {
        val feedValueUpdateCommand = FeedMapper.toFeedValueUpdateCommand(feedId, feedRequest)
        return feedUseCase.updateFeedValue(feedValueUpdateCommand, actorData).map { FeedMapper.toFeedResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.FEED, "feedId")
    @GetMapping("/{feedId}")
    fun getFeed(@PathVariable feedId: String, actorData: ActorData): Mono<FeedResponse> {
        return feedUseCase.getFeedByFeedId(FeedId(feedId), actorData)
            .map { FeedMapper.toFeedResponse(it) }
    }
}

