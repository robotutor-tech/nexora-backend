package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.CreateDeviceTokenUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.TokenMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponsesDto
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextMapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth/tokens")
class TokenController(private val createDeviceTokenUseCase: CreateDeviceTokenUseCase) {

    @PostMapping("/device")
    fun createDeviceActorToken(actorData: ActorData, invitationData: InvitationData): Mono<TokenResponsesDto> {
        val actorContext = ActorContext(
            actorId = actorData.actorId,
            roleId = actorData.role.roleId,
            principalContext = PrincipalContextMapper.toActorPrincipalContext(actorData.principal)
        )
        return createDeviceTokenUseCase.createDeviceToken(actorContext, invitationData)
            .map { TokenMapper.toTokenResponsesDto(it) }
    }
}
