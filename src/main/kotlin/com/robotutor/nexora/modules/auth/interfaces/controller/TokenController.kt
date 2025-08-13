package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthValidationView
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.PremisesActorRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenView
import com.robotutor.nexora.modules.auth.services.InvitationService
import com.robotutor.nexora.modules.auth.services.TokenService
import com.robotutor.nexora.common.security.models.InvitationData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth/tokens")
class TokenController(
    private val tokenService: TokenService,
    private val invitationService: InvitationService,
) {

    @PostMapping
    fun generatePremisesActorToken(
        @RequestBody @Validated premisesActorRequest: PremisesActorRequest,
        @RequestHeader("authorization") token: String = "",
    ): Mono<TokenView> {
        return tokenService.generatePremisesActorToken(token.removePrefix("Bearer").trim(), premisesActorRequest)
            .map { TokenView.Companion.from(it) }
    }

    @PostMapping("/device")
    fun generateDevicePremisesActorToken(
        @RequestBody @Validated premisesActorRequest: PremisesActorRequest,
        invitationData: InvitationData
    ): Mono<TokenView> {
        return tokenService.generateDevicePremisesActorToken(premisesActorRequest, invitationData)
            .flatMap { token ->
                invitationService.markAsAccepted(invitationData).map { TokenView.Companion.from(token) }
            }
    }

    @GetMapping("/validate")
    fun validate(@RequestHeader("authorization") token: String = ""): Mono<AuthValidationView> {
        return tokenService.validate(token.removePrefix("Bearer").trim())
            .map { AuthValidationView.Companion.from(it) }
    }
}
