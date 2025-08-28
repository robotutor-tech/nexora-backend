package com.robotutor.nexora.modules.device.adapters.facade

import com.robotutor.nexora.modules.auth.interfaces.controller.TokenController
import com.robotutor.nexora.modules.device.application.facade.TokenFacade
import com.robotutor.nexora.modules.device.application.facade.dto.DeviceTokens
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TokenApiFacade(private val tokenController: TokenController) : TokenFacade {
    override fun generateDeviceToken(actorData: ActorData): Mono<DeviceTokens> {
        return ContextDataResolver.getInvitationData()
            .flatMap { invitationData ->
                tokenController.createDeviceActorToken(actorData, invitationData)
            }
            .map { tokens -> DeviceTokens(tokens.token, tokens.refreshToken) }
    }
}