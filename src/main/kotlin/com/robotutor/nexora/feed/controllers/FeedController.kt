package com.robotutor.nexora.feed.controllers

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.feed.controllers.view.FeedView
import com.robotutor.nexora.feed.services.FeedService
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
}