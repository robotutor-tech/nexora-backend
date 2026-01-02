package com.robotutor.nexora.context.iam.application.service

import com.robotutor.nexora.context.iam.application.command.AuthorizeResourceCommand
import com.robotutor.nexora.context.iam.application.command.GetAuthorizedResourcesQuery
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.Resources
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
// TODO: application service can't use another another application service
class AuthorizeResourceService(private val actorService: ActorService) {
    fun execute(command: AuthorizeResourceCommand): Mono<Boolean> {
        return actorService.getActorPermissions(command.actorData.actorId)
            .map { permissions -> permissions.authorize(command.resource) }
    }

    fun execute(command: GetAuthorizedResourcesQuery): Mono<Resources<ResourceId>> {
        return actorService.getActorPermissions(command.actorId)
            .map { permissions -> permissions.getResources(command.action, command.type) }
    }
}
