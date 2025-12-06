package com.robotutor.nexora.context.iam.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.context.iam.application.command.InvitationCommand
import com.robotutor.nexora.context.iam.application.dto.TokenResponse
import com.robotutor.nexora.context.iam.domain.entity.Invitation
import com.robotutor.nexora.context.iam.domain.entity.InvitationStatus
import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.entity.TokenType
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.repository.InvitationRepository
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.model.*
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
    private val tokenUseCase: TokenUseCase,
    private val eventPublisher: EventPublisher<IAMEvent>,
    private val resourceCreatedEventPublisher: EventPublisher<ResourceCreatedEvent>
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
                val resourceCreatedEvent = ResourceCreatedEvent(ResourceType.INVITATION, ResourceId(invitationId.value))
                invitationRepository.save(invitation).map { invitation }
                    .publishEvent(resourceCreatedEventPublisher, resourceCreatedEvent)
                    .publishEvents(eventPublisher)
                    .map { Pair(invitation, TokenResponse.from(token)) }
            }
            .logOnSuccess(logger, "Successfully created invitation")
            .logOnError(logger, "", "Failed to create invitation")
    }

    fun getInvitations(invitationIds: List<InvitationId>): Flux<Pair<Invitation, TokenResponse>> {
        return invitationRepository.findAllByInvitationIdInAndStatus(invitationIds, InvitationStatus.INVITED)
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
            .publishEvents(eventPublisher)
    }
}

