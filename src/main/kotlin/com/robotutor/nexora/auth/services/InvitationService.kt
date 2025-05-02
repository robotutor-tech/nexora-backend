package com.robotutor.nexora.auth.services

import com.robotutor.nexora.auth.controllers.views.DeviceInvitationRequest
import com.robotutor.nexora.auth.controllers.views.UserInvitationRequest
import com.robotutor.nexora.auth.models.IdType
import com.robotutor.nexora.auth.models.DeviceInvitation
import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.auth.models.UserInvitation
import com.robotutor.nexora.auth.repositories.DeviceInvitationRepository
import com.robotutor.nexora.auth.repositories.UserInvitationRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class InvitationService(
    private val idGeneratorService: IdGeneratorService,
    private val deviceInvitationRepository: DeviceInvitationRepository,
    private val userInvitationRepository: UserInvitationRepository
) {
    val logger = Logger(this::class.java)

    fun crateDeviceInvitation(
        invitationRequest: DeviceInvitationRequest,
        userData: PremisesActorData
    ): Mono<DeviceInvitation> {
        return idGeneratorService.generateId(IdType.DEVICE_INVITATION_ID)
            .map { invitationId ->
                DeviceInvitation.from(invitationId, invitationRequest, userData)
            }
            .flatMap { deviceInvitationRepository.save(it) }
            .logOnSuccess(logger, "Successfully created device invitation")
            .logOnError(logger, "", "Failed to create device invitation")
    }

    fun getDeviceInvitations(userData: PremisesActorData): Flux<DeviceInvitation> {
        return deviceInvitationRepository.findAllByPremisesIdAndInvitedByAndStatus(
            userData.premisesId,
            userData.actorId
        )
    }

    fun crateUserInvitation(
        userInvitationRequest: UserInvitationRequest,
        userData: PremisesActorData
    ): Mono<UserInvitation> {
        // TODO: Check if already multiple invitation is present
        return idGeneratorService.generateId(IdType.USER_INVITATION_ID)
            .map { invitationId ->
                UserInvitation.from(invitationId, userInvitationRequest, userData)
            }
            .flatMap { userInvitationRepository.save(it) }
            .logOnSuccess(logger, "Successfully created user invitation")
            .logOnError(logger, "", "Failed to create user invitation")
    }

    fun getDeviceInvitation(invitationId: InvitationId): Mono<DeviceInvitation> {
        return deviceInvitationRepository.findByInvitationIdAndStatus(invitationId)
    }

    fun markAsAccepted(invitationData: InvitationData): Mono<DeviceInvitation> {
        return deviceInvitationRepository.findByInvitationIdAndStatus(invitationData.invitationId)
            .map { it.markAsAccepted() as DeviceInvitation }
            .flatMap { deviceInvitationRepository.save(it) }
    }
}

