package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.usecase.AuthenticateAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.RegisterAccountUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.view.RegisterAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.AccountMapper
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.TokenMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/accounts")
class AccountController(
    private val registerAccountUseCase: RegisterAccountUseCase,
    private val authenticateAccountUseCase: AuthenticateAccountUseCase
) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated registerAccountRequest: RegisterAccountRequest): Mono<AccountResponse> {
        val command = AccountMapper.toRegisterAccountCommand(registerAccountRequest)
        return registerAccountUseCase.execute(command)
            .map { AccountMapper.toAccountResponse(it) }
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody @Validated authenticateAccountRequest: AuthenticateAccountRequest): Mono<TokenResponses> {
        val command = AccountMapper.toAuthenticateAccountCommand(authenticateAccountRequest)
        return authenticateAccountUseCase.execute(command)
            .map { TokenMapper.toTokenResponses(it) }
    }
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
