package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.AuthDeviceRegisterCommand
import com.robotutor.nexora.modules.auth.application.command.DeviceLoginCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.domain.entity.AuthDevice
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.modules.auth.domain.event.AuthEvent
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.repository.AuthDeviceRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthDeviceUseCase(
    private val tokenUseCase: TokenUseCase,
    private val authDeviceRepository: AuthDeviceRepository,
    private val eventPublisher: EventPublisher<AuthEvent>,
    private val invitationUseCase: InvitationUseCase
) {
    private val logger = Logger(this::class.java)

    fun deviceLogin(deviceLoginCommand: DeviceLoginCommand): Mono<TokenResponses> {
        println("deviceLogin: $deviceLoginCommand")
        return authDeviceRepository.findByDeviceIdAndDeviceSecret(
            deviceLoginCommand.deviceId,
            deviceLoginCommand.deviceSecret
        )
            .switchIfEmpty(createMonoError(UnAuthorizedException(NexoraError.NEXORA0207)))
            .flatMap { authDevice ->
                tokenUseCase.generateTokenWithRefreshToken(
                    principalType = TokenPrincipalType.ACTOR,
                    principalContext = ActorContext(
                        actorId = authDevice.actorId,
                        roleId = authDevice.roleId,
                        principalContext = DeviceContext(deviceId = deviceLoginCommand.deviceId)
                    )
                )
            }
            .logOnSuccess(logger, "Successfully logged in device")
            .logOnError(logger, "", "Failed to log in device")
    }

    fun register(command: AuthDeviceRegisterCommand, invitationData: InvitationData): Mono<AuthDevice> {
        val authDevice = AuthDevice.register(command.deviceId, command.actorId, command.roleId)
        return authDeviceRepository.save(authDevice).map { authDevice }
            .publishEvents(eventPublisher)
            .flatMap { authDevice ->
                invitationUseCase.markAsAccepted(invitationData.invitationId)
                    .map { authDevice }
            }
            .logOnSuccess(logger, "Successfully registered auth device for ${command.deviceId}")
            .logOnError(logger, "", "Failed to register auth device for ${command.deviceId}")
    }
}