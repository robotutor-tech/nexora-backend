package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.AuthUserService
import com.robotutor.nexora.modules.auth.application.RefreshTokenUseCase
import com.robotutor.nexora.modules.auth.application.ValidateTokenUseCase
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthLoginRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserResponseDto
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenResponsesDto
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenValidationResultDto
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.AuthUserMapper
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.TokenMapper
import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authUserService: AuthUserService,
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase
) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated authUserRequest: AuthUserRequest): Mono<AuthUserResponseDto> {
        val registerAuthUserCommand = AuthUserMapper.toRegisterAuthUserCommand(authUserRequest)
        return authUserService.register(registerAuthUserCommand)
            .map { AuthUserMapper.toAuthUserResponseDto(it) }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Validated authLoginRequest: AuthLoginRequest): Mono<TokenResponsesDto> {
        val loginCommand = AuthUserMapper.toLoginCommand(authLoginRequest)
        return authUserService.login(loginCommand)
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
