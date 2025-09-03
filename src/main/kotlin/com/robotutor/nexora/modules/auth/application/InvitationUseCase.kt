package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.InvitationCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponse
import com.robotutor.nexora.modules.auth.domain.entity.Invitation
import com.robotutor.nexora.modules.auth.domain.entity.InvitationStatus
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.modules.auth.domain.entity.TokenType
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.repository.InvitationRepository
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InvitationContext
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
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
        val invitationId = InvitationId(UUID.randomUUID().toString())
        return tokenUseCase.generateToken(
            tokenType = TokenType.AUTHORIZATION,
            principalType = TokenPrincipalType.INVITATION,
            principalContext = InvitationContext(invitationId)
        )
            .flatMap { token ->
                val invitation = Invitation.create(
                    invitationId = invitationId,
                    premisesId = actorData.premisesId,
                    name = invitationCommand.name,
                    zoneId = invitationCommand.zoneId,
                    invitedBy = actorData.actorId,
                    tokenId = token.tokenId
                )
                invitationRepository.save(invitation).map { invitation }
//                    .publishEvents()
                    .map { Pair(invitation, TokenResponse.from(token)) }
            }
            .logOnSuccess(logger, "Successfully created invitation")
            .logOnError(logger, "", "Failed to create invitation")
    }

    fun getInvitations(actorData: ActorData): Flux<Pair<Invitation, TokenResponse>> {
        return invitationRepository.findAllByInvitedByAndStatus(actorData.actorId, InvitationStatus.INVITED)
            .collectList()
            .flatMapMany { invitations ->
                val tokenIds = invitations.mapNotNull { invitation -> invitation.tokenId }
                tokenUseCase.getAllTokenByTokenIdIn(tokenIds)
                    .map { token ->
                        val invitation = invitations.find { invitation -> invitation.tokenId == token.tokenId }!!
                        Pair(invitation, TokenResponse.from(token))
                    }
            }
    }

    fun getInvitation(invitationId: InvitationId): Mono<Invitation> {
        return invitationRepository.findByInvitationIdAndStatus(invitationId, InvitationStatus.INVITED)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0204)))
    }

    fun markAsAccepted(invitationId: InvitationId): Mono<Invitation> {
        return getInvitation(invitationId)
            .map { invitation -> invitation.markAsAccepted() }
            .flatMap { invitation -> invitationRepository.save(invitation).map { invitation } }
//            .publishEvents()
    }
}

