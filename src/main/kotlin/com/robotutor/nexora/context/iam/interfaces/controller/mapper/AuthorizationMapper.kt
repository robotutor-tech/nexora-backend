package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.AuthorizeResourceCommand
import com.robotutor.nexora.context.iam.domain.vo.Resource
import com.robotutor.nexora.context.iam.interfaces.controller.view.ResourceRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.ResourceResponse
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceId

object AuthorizationMapper {
    fun toAuthorizeResourceCommand(resourceRequest: ResourceRequest, actorData: ActorData): AuthorizeResourceCommand {
        return AuthorizeResourceCommand(
            actorData = actorData,
            resource = Resource(
                resourceId = ResourceId(resourceRequest.resourceId),
                premisesId = actorData.premisesId,
                type = resourceRequest.resourceType,
                action = resourceRequest.actionType
            )
        )
    }

    fun toResourceResponse(result: Boolean): ResourceResponse {
        return ResourceResponse(result)
    }
}