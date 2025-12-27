package com.robotutor.nexora.context.feed.interfaces.controller

import com.robotutor.nexora.context.feed.application.usecase.FeedUseCase
import com.robotutor.nexora.context.feed.application.usecase.RegisterFeedUseCase
import com.robotutor.nexora.context.feed.interfaces.controller.mapper.FeedMapper
import com.robotutor.nexora.context.feed.interfaces.controller.view.FeedResponse
import com.robotutor.nexora.context.feed.interfaces.controller.view.RegisterFeedsRequest
import com.robotutor.nexora.shared.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/feeds")
class FeedController(private val feedUseCase: FeedUseCase, private val registerFeedUseCase: RegisterFeedUseCase) {
    @HttpAuthorize(ActionType.READ, ResourceType.FEED)
    @GetMapping
    fun getFeeds(ActorData: ActorData, resources: AuthorizedResources): Flux<FeedResponse> {
        val query = FeedMapper.toGetFeedsQuery(resources, ActorData)
        return feedUseCase.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }

    @HttpAuthorize(ActionType.CREATE, ResourceType.FEED)
    @PostMapping
    fun registerFeeds(@RequestBody @Validated request: RegisterFeedsRequest, ActorData: ActorData): Flux<FeedResponse> {
        val query = FeedMapper.toRegisterFeedsCommand(request, ActorData)
        return registerFeedUseCase.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }
}

