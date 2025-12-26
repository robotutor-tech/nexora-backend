package com.robotutor.nexora.context.device.interfaces.controller

import com.robotutor.nexora.context.device.application.usecase.FeedUseCase
import com.robotutor.nexora.context.device.interfaces.controller.mapper.FeedMapper
import com.robotutor.nexora.context.device.interfaces.controller.view.FeedResponse
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/feeds")
class FeedController(private val feedUseCase: FeedUseCase) {
    @Authorize(ActionType.READ, ResourceType.FEED)
    @GetMapping
    fun getFeeds(actorData: ActorData, resources: AuthorizedResources): Flux<FeedResponse> {
        val query = FeedMapper.toGetFeedsQuery(resources, actorData)
        return feedUseCase.execute(query)
            .map { FeedMapper.toFeedResponse(it) }
    }
}