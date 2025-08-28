package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.modules.auth.application.command.InvitationCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponse
import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.repository.InvitationRepository
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InvitationContext
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class InvitationUseCase(
    private val invitationRepository: InvitationRepository,
    private val tokenUseCase: TokenUseCase
) {
    private val logger = Logger(this::class.java)

    fun createInvitation(
        invitationCommand: InvitationCommand,
        actorData: ActorData
    ): Mono<Pair<Invitation, TokenResponse>> {
        val invitationData = Invitation(
            invitationId = InvitationId(UUID.randomUUID().toString()),
            premisesId = actorData.premisesId,
            name = invitationCommand.name,
            zoneId = invitationCommand.zoneId,
            invitedBy = actorData.actorId,
        )
        return invitationRepository.save(invitationData)
            .flatMap { invitation ->
                tokenUseCase.generateToken(
                    tokenType = TokenType.AUTHORIZATION,
                    principalType = TokenPrincipalType.INVITATION,
                    principalContext = InvitationContext(invitation.invitationId)
                )
                    .map { token -> Pair(invitation, TokenResponse.from(token)) }
            }
            .logOnSuccess(logger, "Successfully created invitation")
            .logOnError(logger, "", "Failed to create invitation")
    }

    fun getInvitation(invitationId: InvitationId): Mono<Invitation> {
        return invitationRepository.findByInvitationId(invitationId)
    }

    fun markAsAccepted(invitationId: InvitationId): Mono<Invitation> {
        return getInvitation(invitationId)
//            .map { invitation -> invitation.markAsAccepted() }
//            .flatMap { invitation -> invitationRepository.save(invitation).map { invitation } }
//            .publishEvents()
    }
}

