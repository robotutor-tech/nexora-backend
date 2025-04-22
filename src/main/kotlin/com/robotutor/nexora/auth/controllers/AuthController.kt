package com.robotutor.nexora.auth.controllers

import com.robotutor.nexora.auth.controllers.views.AuthLoginRequest
import com.robotutor.nexora.auth.controllers.views.AuthUserRequest
import com.robotutor.nexora.auth.controllers.views.AuthValidationView
import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.auth.services.AuthService
import com.robotutor.nexora.auth.services.TokenService
import com.robotutor.nexora.utils.models.UserId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val tokenService: TokenService
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
}