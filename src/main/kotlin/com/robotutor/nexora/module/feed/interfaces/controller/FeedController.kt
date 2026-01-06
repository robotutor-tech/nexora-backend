package com.robotutor.nexora.module.feed.interfaces.controller

import com.robotutor.nexora.common.resource.annotation.ResourceSelector
import com.robotutor.nexora.module.feed.application.service.GetFeedService
import com.robotutor.nexora.module.feed.application.service.RegisterFeedService
import com.robotutor.nexora.module.feed.application.service.UpdateFeedValueService
import com.robotutor.nexora.module.feed.interfaces.controller.mapper.FeedMapper
import com.robotutor.nexora.module.feed.interfaces.controller.view.FeedResponse
import com.robotutor.nexora.module.feed.interfaces.controller.view.RegisterFeedsRequest
import com.robotutor.nexora.module.feed.interfaces.controller.view.UpdateValueRequest
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/feeds")
class FeedController(
    private val getFeedService: GetFeedService,
    private val registerFeedService: RegisterFeedService,
    private val updateFeedValueService: UpdateFeedValueService
) {
    @GetMapping
    fun getFeeds(
        actorData: ActorData,
        @ResourceSelector(ActionType.READ, ResourceType.FEED) resources: Resources
    ): Flux<FeedResponse> {
        val query = FeedMapper.toGetFeedsQuery(resources, actorData)
        return getFeedService.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }

    @PostMapping
    fun registerFeeds(@RequestBody @Validated request: RegisterFeedsRequest, actorData: ActorData): Flux<FeedResponse> {
        val query = FeedMapper.toRegisterFeedsCommand(request, actorData)
        return registerFeedService.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }

    @PatchMapping("{feedId}/value")
    fun updateValue(
        @PathVariable feedId: String,
        @RequestBody @Validated updateValueRequest: UpdateValueRequest,
        actorData: ActorData
    ): Mono<FeedResponse> {
        val command = FeedMapper.toUpdateValueCommand(feedId, updateValueRequest, actorData)
        return updateFeedValueService.execute(command)
            .map { FeedMapper.toFeedResponse(it) }
    }
}

