package com.robotutor.nexora.context.feed.interfaces.controller

import com.robotutor.nexora.common.security.domain.vo.AuthorizedResources
import com.robotutor.nexora.context.feed.application.usecase.FeedUseCase
import com.robotutor.nexora.context.feed.application.usecase.RegisterFeedUseCase
import com.robotutor.nexora.context.feed.interfaces.controller.mapper.FeedMapper
import com.robotutor.nexora.context.feed.interfaces.controller.view.FeedResponse
import com.robotutor.nexora.context.feed.interfaces.controller.view.RegisterFeedsRequest
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/feeds")
class FeedController(private val feedUseCase: FeedUseCase, private val registerFeedUseCase: RegisterFeedUseCase) {
    @GetMapping
    fun getFeeds(actorData: ActorData, resources: AuthorizedResources): Flux<FeedResponse> {
        val query = FeedMapper.toGetFeedsQuery(resources, actorData)
        return feedUseCase.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }

    @PostMapping
    fun registerFeeds(@RequestBody @Validated request: RegisterFeedsRequest, actorData: ActorData): Flux<FeedResponse> {
        val query = FeedMapper.toRegisterFeedsCommand(request, actorData)
        return registerFeedUseCase.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }
}

