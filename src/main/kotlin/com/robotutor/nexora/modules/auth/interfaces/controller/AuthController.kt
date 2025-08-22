package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.ActorLoginUseCase
import com.robotutor.nexora.modules.auth.application.AuthUserUseCase
import com.robotutor.nexora.modules.auth.application.RefreshTokenUseCase
import com.robotutor.nexora.modules.auth.application.ValidateTokenUseCase
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.*
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.AuthUserMapper
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.TokenMapper
import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authUserUseCase: AuthUserUseCase,
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val actorLoginUseCase: ActorLoginUseCase
) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated authUserRequest: AuthUserRequest): Mono<AuthUserResponseDto> {
        val registerAuthUserCommand = AuthUserMapper.toRegisterAuthUserCommand(authUserRequest)
        return authUserUseCase.register(registerAuthUserCommand)
            .map { AuthUserMapper.toAuthUserResponseDto(it) }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Validated authLoginRequest: AuthLoginRequest): Mono<TokenResponsesDto> {
        val loginCommand = AuthUserMapper.toLoginCommand(authLoginRequest)
        return authUserUseCase.login(loginCommand)
            .map { TokenMapper.toTokenResponsesDto(it) }
    }

    @PostMapping("/login/actor")
    fun actorLogin(
        @RequestHeader("authorization") token: String,
        @RequestBody @Validated actorLoginRequest: ActorLoginRequest,
        userData: UserData
    ): Mono<TokenResponsesDto> {
        val actorLoginCommand = AuthUserMapper.toActorLoginCommand(actorLoginRequest, userData, token)
        return actorLoginUseCase.actorLogin(actorLoginCommand)
            .map { TokenMapper.toTokenResponsesDto(it) }
    }

    @GetMapping("/validate")
    fun validate(@RequestHeader("authorization") token: String = ""): Mono<TokenValidationResultDto> {
        if (!token.startsWith("Bearer ")) {
            return createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
        }
        val validateTokenCommand = TokenMapper.toValidateTokenCommand(token)
        return validateTokenUseCase.validate(validateTokenCommand)
            .map { TokenMapper.toValidateTokenResultDto(it) }
    }

    @GetMapping("/refresh")
    fun refresh(@RequestHeader("authorization") token: String = ""): Mono<TokenResponsesDto> {
        if (!token.startsWith("Bearer ")) {
            return createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
        }
        val refreshTokenCommand = TokenMapper.toRefreshTokenCommand(token)
        return refreshTokenUseCase.refresh(refreshTokenCommand)
            .map { TokenMapper.toTokenResponsesDto(it) }
    }
}
