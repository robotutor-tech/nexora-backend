package com.robotutor.nexora.auth.services

import com.robotutor.nexora.auth.controllers.views.DeviceInvitationRequest
import com.robotutor.nexora.auth.controllers.views.UserInvitationRequest
import com.robotutor.nexora.auth.models.IdType
import com.robotutor.nexora.auth.models.Invitation
import com.robotutor.nexora.auth.repositories.InvitationRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.models.PremisesActorData
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

    fun crateDeviceInvitation(
        invitationRequest: DeviceInvitationRequest,
        userData: PremisesActorData
    ): Mono<Invitation> {
        return idGeneratorService.generateId(IdType.INVITATION_ID)
            .map { invitationId ->
                Invitation.deviceInvitation(invitationId, invitationRequest, userData)
            }
            .flatMap { invitationRepository.save(it) }
            .logOnSuccess(logger, "Successfully created device invitation")
            .logOnError(logger, "", "Failed to create device invitation")
    }

    fun getInvitation(userData: PremisesActorData): Flux<Invitation> {
        return invitationRepository.findAllByPremisesIdAndCreatedBy(userData.premisesId, userData.actorId)
    }

//    fun getInvitationByToken(token: Token, modelNo: String): Mono<Invitation> {
//        return invitationRepository.findByTokenIdAndModelNo(token.tokenId, modelNo)
//    }

    fun crateUserInvitation(
        userInvitationRequest: UserInvitationRequest,
        userData: PremisesActorData
    ): Mono<Invitation> {
        // TODO: Check if already multiple invitation is present
        return idGeneratorService.generateId(IdType.INVITATION_ID)
            .map { invitationId ->
                Invitation.userInvitation(invitationId, userInvitationRequest, userData)
            }
            .flatMap { invitationRepository.save(it) }
            .logOnSuccess(logger, "Successfully created device invitation")
            .logOnError(logger, "", "Failed to create device invitation")
    }
}

