package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateDeviceTokenUseCase(
    private val tokenUseCase: TokenUseCase,
    private val invitationUseCase: InvitationUseCase
) {
    private val logger = Logger(this::class.java)

    fun createDeviceToken(actorContext: ActorContext, invitationData: InvitationData): Mono<TokenResponses> {
        return tokenUseCase.generateTokenWithRefreshToken(TokenPrincipalType.ACTOR, actorContext)
            .flatMap { tokenResponses ->
                invitationUseCase.markAsAccepted(invitationData.invitationId)
                    .map { tokenResponses }
            }
            .logOnSuccess(logger, "Successfully created device token")
            .logOnError(logger, "", "Failed to create device token")
    }
}