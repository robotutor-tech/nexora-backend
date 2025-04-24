package com.robotutor.nexora.auth.services

import com.robotutor.nexora.auth.controllers.views.DeviceRequest
import com.robotutor.nexora.auth.models.IdType
import com.robotutor.nexora.auth.models.Invitation
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.repositories.InvitationRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.models.UserPremisesData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class InvitationService(
    private val idGeneratorService: IdGeneratorService,
    private val invitationRepository: InvitationRepository
) {
    val logger = Logger(this::class.java)

    fun crateInvitation(deviceRequest: DeviceRequest, token: Token, userData: UserPremisesData): Mono<Invitation> {
        return idGeneratorService.generateId(IdType.INVITATION_ID)
            .flatMap { invitationId ->
                invitationRepository.save(Invitation.from(invitationId, token.tokenId, deviceRequest, userData))
            }
            .logOnSuccess(logger, "Successfully created invitation")
            .logOnError(logger, "", "Failed to create invitation")
    }

    fun getInvitation(userData: UserPremisesData): Flux<Invitation> {
        return invitationRepository.findAllByPremisesIdAndCreatedBy(userData.premisesId, userData.userId)
    }
}
