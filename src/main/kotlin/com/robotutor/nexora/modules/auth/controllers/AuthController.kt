package com.robotutor.nexora.modules.auth.controllers

import com.robotutor.nexora.modules.auth.controllers.views.AuthLoginRequest
import com.robotutor.nexora.modules.auth.controllers.views.AuthUserRequest
import com.robotutor.nexora.modules.auth.controllers.views.PremisesActorDataView
import com.robotutor.nexora.modules.auth.controllers.views.TokenView
import com.robotutor.nexora.modules.auth.exceptions.NexoraError
import com.robotutor.nexora.modules.auth.services.AuthService
import com.robotutor.nexora.modules.auth.services.TokenService
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.models.*
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.UnAuthorizedException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService, private val tokenService: TokenService) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated authUserRequest: AuthUserRequest): Mono<UserId> {
        return authService.register(authUserRequest).map { it.userId }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Validated authLoginRequest: AuthLoginRequest): Mono<TokenView> {
        return authService.login(authLoginRequest)
            .flatMap { tokenService.generateAuthUserToken(it) }
            .map { TokenView.from(it) }
    }

    @GetMapping("/validate")
    fun validate(): Mono<IAuthenticationData> {
        return Mono.deferContextual { ctx ->
            val premisesActorDataOptional = ctx.getOrEmpty<PremisesActorData>(PremisesActorData::class.java)
            val authUserDataOptional = ctx.getOrEmpty<AuthUserData>(AuthUserData::class.java)
            val invitationDataOptional = ctx.getOrEmpty<InvitationData>(InvitationData::class.java)
            when {
                premisesActorDataOptional.isPresent -> createMono(premisesActorDataOptional.get())
                    .map { PremisesActorDataView.from(it) }

                authUserDataOptional.isPresent -> createMono(authUserDataOptional.get())
                invitationDataOptional.isPresent -> createMono(invitationDataOptional.get())
                else -> createMonoError(UnAuthorizedException(NexoraError.NEXORA0203))
            }
        }
    }
}
