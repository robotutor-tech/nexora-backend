package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.usecase.RegisterAccountUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.view.RegisterAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.AccountMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val registerAccountUseCase: RegisterAccountUseCase
) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated registerAccountRequest: RegisterAccountRequest): Mono<AccountResponse> {
        val command = AccountMapper.toRegisterAccountCommand(registerAccountRequest)
        return registerAccountUseCase.execute(command)
            .map { AccountMapper.toAccountResponse(it) }
    }

//    @PostMapping("/login")
//    fun login(@RequestBody @Validated authLoginRequest: AuthLoginRequest): Mono<TokenResponsesDto> {
//        val loginCommand = AccountMapper.toLoginCommand(authLoginRequest)
//        return authUserUseCase.login(loginCommand)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
//
//    @PostMapping("/login/actor")
//    fun actorLogin(
//        @RequestHeader("authorization") token: String,
//        @RequestBody @Validated actorLoginRequest: ActorLoginRequest,
//        userData: UserData
//    ): Mono<TokenResponsesDto> {
//        val actorLoginCommand = AccountMapper.toActorLoginCommand(actorLoginRequest, userData, token)
//        return actorLoginUseCase.actorLogin(actorLoginCommand)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
//
//    @PostMapping("/login/device")
//    fun deviceLogin(
//        @RequestBody @Validated deviceLoginRequest: DeviceLoginRequest,
//    ): Mono<TokenResponsesDto> {
//        val deviceLoginCommand = AuthDeviceMapper.toDeviceLoginCommand(deviceLoginRequest)
//        return authDeviceUseCase.deviceLogin(deviceLoginCommand)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
//
//    @GetMapping("/validate")
//    fun validate(@RequestHeader("authorization") token: String = ""): Mono<TokenValidationResultDto> {
//        if (!token.startsWith("Bearer ")) {
//            return createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
//        }
//        val validateTokenCommand = TokenMapper.toValidateTokenCommand(token)
//        return validateTokenUseCase.validate(validateTokenCommand)
//            .map { TokenMapper.toValidateTokenResultDto(it) }
//    }
//
//    @GetMapping("/refresh")
//    fun refresh(@RequestHeader("authorization") token: String = ""): Mono<TokenResponsesDto> {
//        if (!token.startsWith("Bearer ")) {
//            return createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
//        }
//        val refreshTokenCommand = TokenMapper.toRefreshTokenCommand(token)
//        return refreshTokenUseCase.refresh(refreshTokenCommand)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
//
//    fun registerDevice(@RequestBody @Validated request: AuthDeviceRegisterRequest, invitationData: InvitationData): Mono<AuthDeviceResponse> {
//        val command = AuthDeviceMapper.toAuthDeviceRegisterCommand(request)
//        return authDeviceUseCase.register(command, invitationData)
//            .map { AuthDeviceMapper.toAuthDeviceResponse(it) }
//    }
}
