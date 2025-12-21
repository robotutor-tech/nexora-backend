package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.command.GetAccountQuery
import com.robotutor.nexora.context.iam.application.usecase.account.RotateCredentialUseCase
import com.robotutor.nexora.context.iam.application.usecase.account.GetAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.account.AuthenticateAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.account.RegisterAccountUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.view.RegisterAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.AccountMapper
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.CredentialMapper
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.SessionMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.CredentialRotatedResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/accounts")
class AccountController(
    private val registerAccountUseCase: RegisterAccountUseCase,
    private val authenticateAccountUseCase: AuthenticateAccountUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val rotateCredentialUseCase: RotateCredentialUseCase
) {

    @PostMapping("/register")
    fun registerMachine(@RequestBody @Validated registerAccountRequest: RegisterAccountRequest): Mono<AccountResponse> {
        val command = AccountMapper.toRegisterAccountCommand(registerAccountRequest, null)
        return registerAccountUseCase.execute(command)
            .map { AccountMapper.toAccountResponse(it) }
    }

    @PostMapping("/register/machine")
    fun registerMachine(
        @RequestBody @Validated registerAccountRequest: RegisterAccountRequest,
        actorData: ActorData
    ): Mono<AccountResponse> {
        val command = AccountMapper.toRegisterAccountCommand(registerAccountRequest, actorData)
        return registerAccountUseCase.execute(command)
            .map { AccountMapper.toAccountResponse(it) }
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody @Validated authenticateAccountRequest: AuthenticateAccountRequest): Mono<TokenResponses> {
        val command = AccountMapper.toAuthenticateAccountCommand(authenticateAccountRequest)
        return authenticateAccountUseCase.execute(command)
            .map { SessionMapper.toTokenResponses(it) }
    }

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: String): Mono<AccountResponse> {
        val query = GetAccountQuery(AccountId(accountId))
        return getAccountUseCase.execute(query).map { AccountMapper.toAccountResponse(it) }
    }

    @PatchMapping("/{accountId}/credentials/rotate")
    fun rotateCredentials(@PathVariable accountId: String, actorData: ActorData): Mono<CredentialRotatedResponse> {
        val command = AccountMapper.toRotateCredentialsCommand(accountId, actorData)
        return rotateCredentialUseCase.execute(command)
            .map { CredentialMapper.toCredentialRotatedResponse(it) }
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
