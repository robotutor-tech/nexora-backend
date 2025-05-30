package com.robotutor.nexora.auth.controllers

import com.robotutor.nexora.auth.controllers.views.AuthValidationView
import com.robotutor.nexora.auth.controllers.views.PremisesActorRequest
import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.auth.services.InvitationService
import com.robotutor.nexora.auth.services.TokenService
import com.robotutor.nexora.security.models.InvitationData
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
            .map { TokenView.from(it) }
    }

    @PostMapping("/device")
    fun generateDevicePremisesActorToken(
        @RequestBody @Validated premisesActorRequest: PremisesActorRequest,
        invitationData: InvitationData
    ): Mono<TokenView> {
        return tokenService.generateDevicePremisesActorToken(premisesActorRequest, invitationData)
            .flatMap { token ->
                invitationService.markAsAccepted(invitationData).map { TokenView.from(token) }
            }
    }

    @GetMapping("/validate")
    fun validate(@RequestHeader("authorization") token: String = ""): Mono<AuthValidationView> {
        return tokenService.validate(token.removePrefix("Bearer").trim()).map { AuthValidationView.from(it) }
    }
}
