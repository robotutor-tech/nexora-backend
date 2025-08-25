package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.modules.auth.application.CreateDeviceTokenUseCase
import com.robotutor.nexora.modules.auth.application.command.CreateDeviceTokenCommand
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.DeviceTokenRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenResponsesDto
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.TokenMapper
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.InvitationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth/tokens")
class TokenController(private val createDeviceTokenUseCase: CreateDeviceTokenUseCase) {

    @PostMapping("/device")
    fun createDeviceActorToken(
        @RequestBody @Validated deviceTokenRequest: DeviceTokenRequest,
        invitationData: InvitationData
    ): Mono<TokenResponsesDto> {
        val command = CreateDeviceTokenCommand(DeviceId(deviceTokenRequest.deviceId))
        return createDeviceTokenUseCase.createDeviceToken(command, invitationData)
            .map { TokenMapper.toTokenResponsesDto(it) }
    }
}
