package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.AuthorizeResourceCommand
import com.robotutor.nexora.context.iam.application.command.GetAuthorizedResourcesQuery
import com.robotutor.nexora.context.iam.domain.vo.Resource
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthorizeResourceRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthorizeResourceResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.GetAuthorizedResourcesRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.GetAuthorizedResourcesResponse
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

object AuthorizationMapper {
    fun toAuthorizeResourceCommand(
        authorizeResourceRequest: AuthorizeResourceRequest,
        actorData: ActorData
    ): AuthorizeResourceCommand {
        return AuthorizeResourceCommand(
            ActorData = actorData,
            resource = Resource(
                resourceId = ResourceId(authorizeResourceRequest.resourceId),
                premisesId = actorData.premisesId,
                type = authorizeResourceRequest.resourceType,
                action = authorizeResourceRequest.actionType
            )
        )
    }

    fun toResourceResponse(result: Boolean): AuthorizeResourceResponse {
        return AuthorizeResourceResponse(result)
    }

    fun toGetAuthorizedResourceQuery(
        request: GetAuthorizedResourcesRequest,
        actorData: ActorData
    ): GetAuthorizedResourcesQuery {
        return GetAuthorizedResourcesQuery(actorData.actorId, request.resourceType, request.actionType)
    }

    fun toAuthorizedResourcesResponse(resources: Resources<ResourceId>): GetAuthorizedResourcesResponse {
        return GetAuthorizedResourcesResponse(
            premisesId = resources.premisesId.value,
            resourceType = resources.resourceType,
            actionType = resources.actionType,
            resourceSelector = resources.resourceSelector,
            allowedIds = resources.allowedIds.map { it.value }.toSet(),
            deniedIds = resources.deniedIds.map { it.value }.toSet()
        )
    }
}