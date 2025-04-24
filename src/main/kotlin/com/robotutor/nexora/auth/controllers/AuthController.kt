package com.robotutor.nexora.auth.controllers

import com.robotutor.nexora.auth.controllers.views.*
import com.robotutor.nexora.auth.services.AuthService
import com.robotutor.nexora.auth.services.InvitationService
import com.robotutor.nexora.auth.services.TokenService
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.security.models.UserPremisesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val tokenService: TokenService,
    private val invitationService: InvitationService,
) {
    @PostMapping("/register")
    fun register(@RequestBody @Validated authUserRequest: AuthUserRequest): Mono<UserId> {
        return authService.register(authUserRequest).map { it.userId }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Validated authLoginRequest: AuthLoginRequest): Mono<TokenView> {
        return authService.login(authLoginRequest).map { TokenView.from(it) }
    }

    @GetMapping("/validate")
    fun validate(
        @RequestParam full: String = "",
        @RequestHeader("authorization") token: String = ""
    ): Mono<AuthValidationView> {
        return tokenService.validate(token.removePrefix("Bearer").trim(), full != "false")
            .map { AuthValidationView.from(it) }
    }

    @PostMapping("/token")
    fun generateFullSecuredToken(
        @RequestBody @Validated tokenRequest: TokenRequest,
        @RequestHeader("authorization") token: String = ""
    ): Mono<TokenView> {
        return tokenService.generateFullSecuredToken(token.removePrefix("Bearer").trim(), tokenRequest)
            .map { TokenView.from(it) }
    }

    @PostMapping("/devices/invitation")
    fun deviceInvitation(
        @RequestBody @Validated deviceRequest: DeviceRequest,
        userData: UserPremisesData
    ): Mono<InvitationView> {
        return tokenService.generateInvitationToken(userData)
            .flatMap { token ->
                invitationService.crateInvitation(deviceRequest, token, userData)
                    .map { InvitationView.from(token, it) }
            }
    }

    @GetMapping("/devices/invitation")
    fun getDeviceInvitation(userData: UserPremisesData): Flux<InvitationView> {
        return invitationService.getInvitation(userData)
            .flatMap { invitation ->
                tokenService.getInvitationToken(invitation)
                    .map { token -> InvitationView.from(token, invitation) }
            }
    }
}
