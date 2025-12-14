package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.usecase.AuthorizeResourceUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.AuthorizationMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.ResourceRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.ResourceResponse
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/authorize")
class AuthorizationController(
    private val authorizeResourceUseCase: AuthorizeResourceUseCase,
) {
    @PostMapping
    fun authorize(
        @RequestBody @Validated resourceRequest: ResourceRequest,
        actorData: ActorData
    ): Mono<ResourceResponse> {
        val command = AuthorizationMapper.toAuthorizeResourceCommand(resourceRequest, actorData)
        return authorizeResourceUseCase.execute(command)
            .map { AuthorizationMapper.toResourceResponse(it) }
    }
}

