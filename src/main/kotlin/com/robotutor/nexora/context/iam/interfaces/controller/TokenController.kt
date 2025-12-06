package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.usecase.ValidateTokenUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.TokenMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenValidationResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/tokens")
class TokenController(
    private val validateTokenUseCase: ValidateTokenUseCase,
) {

    @GetMapping("/validate")
    fun validate(@RequestHeader("authorization") token: String = ""): Mono<TokenValidationResponse> {
        val validateTokenCommand = TokenMapper.toValidateTokenCommand(token)
        return validateTokenUseCase.execute(validateTokenCommand)
            .map { TokenMapper.toValidateTokenResponse(it) }
    }

//    @PostMapping("/device")
//    fun createDeviceActorToken(actorData: ActorData, invitationData: InvitationData): Mono<TokenResponses> {
//        val actorContext = ActorContext(
//            actorId = actorData.actorId,
//            roleId = actorData.role.roleId,
//            principalContext = PrincipalContextMapper.toActorPrincipalContext(actorData.principal)
//        )
//        return createDeviceTokenUseCase.createDeviceToken(actorContext, invitationData)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
}
