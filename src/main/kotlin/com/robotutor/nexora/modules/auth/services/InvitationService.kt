package com.robotutor.nexora.modules.auth.services

import com.robotutor.nexora.modules.auth.controllers.views.DeviceInvitationRequest
import com.robotutor.nexora.modules.auth.controllers.views.UserInvitationRequest
import com.robotutor.nexora.modules.auth.models.IdType
import com.robotutor.nexora.modules.auth.models.DeviceInvitation
import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.modules.auth.models.UserInvitation
import com.robotutor.nexora.modules.auth.repositories.DeviceInvitationRepository
import com.robotutor.nexora.modules.auth.repositories.UserInvitationRepository
import com.robotutor.nexora.modules.auth.exceptions.NexoraError
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.services.IdGeneratorService
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.DataNotFoundException
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
            .flatMap {
                deviceInvitationRepository.save(it)
                    .auditOnSuccess(
                        "DEVICE_INVITATION_CREATED",
                        mapOf("invitationId" to it.invitationId, "zoneId" to it.zoneId, "name" to it.name)
                    )
            }
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
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0204)))
    }

    fun markAsAccepted(invitationData: InvitationData): Mono<DeviceInvitation> {
        return deviceInvitationRepository.findByInvitationIdAndStatus(invitationData.invitationId)
            .map { it.markAsAccepted() as DeviceInvitation }
            .flatMap { deviceInvitationRepository.save(it) }
    }
}

