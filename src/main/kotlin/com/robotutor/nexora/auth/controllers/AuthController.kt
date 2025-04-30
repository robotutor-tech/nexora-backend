package com.robotutor.nexora.auth.controllers

import com.robotutor.nexora.auth.controllers.views.AuthLoginRequest
import com.robotutor.nexora.auth.controllers.views.AuthUserRequest
import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.auth.services.AuthService
import com.robotutor.nexora.auth.services.TokenService
import com.robotutor.nexora.security.models.UserId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
}
