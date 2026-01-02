package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.service.AuthorizeResourceService
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.AuthorizationMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthorizeResourceRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthorizeResourceResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.GetAuthorizedResourcesRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.GetAuthorizedResourcesResponse
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/resources")
class AuthorizationController(
    private val authorizeResourceService: AuthorizeResourceService,
) {
    @PostMapping
    fun getResources(
        @RequestBody @Validated getAuthorizedResourcesRequest: GetAuthorizedResourcesRequest,
        actorData: ActorData
    ): Mono<GetAuthorizedResourcesResponse> {
        val query = AuthorizationMapper.toGetAuthorizedResourceQuery(getAuthorizedResourcesRequest, actorData)
        return authorizeResourceService.execute(query)
            .map { AuthorizationMapper.toAuthorizedResourcesResponse(it) }
    }

    @PostMapping("/authorize")
    fun authorize(
        @RequestBody @Validated authorizeResourceRequest: AuthorizeResourceRequest,
        actorData: ActorData
    ): Mono<AuthorizeResourceResponse> {
        val command = AuthorizationMapper.toAuthorizeResourceCommand(authorizeResourceRequest, actorData)
        return authorizeResourceService.execute(command)
            .map { AuthorizationMapper.toResourceResponse(it) }
    }
}

