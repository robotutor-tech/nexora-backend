package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.AuthorizeResourceCommand
import com.robotutor.nexora.context.iam.application.service.ActorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthorizeResourceUseCase(private val actorService: ActorService) {
    fun execute(command: AuthorizeResourceCommand): Mono<Boolean> {
        return actorService.getActorPermissions(command.actorData.actorId)
            .map { permissions -> permissions.authorize(command.resource) }
    }
}
