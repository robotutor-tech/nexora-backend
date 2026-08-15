package com.robotutor.nexora.module.identity.interfaces.controller

import com.robotutor.nexora.module.identity.application.command.GetAccountQuery
import com.robotutor.nexora.module.identity.application.service.account.AuthenticateAccountService
import com.robotutor.nexora.module.identity.application.service.account.GetAccountService
import com.robotutor.nexora.module.identity.application.service.account.RegisterUserAccountService
import com.robotutor.nexora.module.identity.application.service.account.RotateCredentialService
import com.robotutor.nexora.module.identity.interfaces.controller.mapper.AccountMapper
import com.robotutor.nexora.module.identity.interfaces.controller.mapper.SessionMapper
import com.robotutor.nexora.module.identity.interfaces.controller.view.*
import com.robotutor.nexora.shared.domain.vo.AccountId
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/identity/accounts")
class AccountController(
    private val registerUserAccountService: RegisterUserAccountService,
    private val authenticateAccountService: AuthenticateAccountService,
    private val getAccountService: GetAccountService,
    private val rotateCredentialService: RotateCredentialService
) {

    @PostMapping("/register")
    fun registerMachine(@RequestBody registerUserAccountRequest: RegisterUserAccountRequest): Mono<AccountResponse> {
        val command = AccountMapper.toRegisterUserAccountCommand(registerUserAccountRequest)
        return registerUserAccountService.execute(command)
            .map { AccountMapper.toAccountResponse(it) }
    }

//    @PostMapping("/register/machine")
//    fun registerMachine(
//        @RequestBody @Validated registerUserAccountRequest: RegisterUserAccountRequest,
//        actorData: ActorData
//    ): Mono<AccountResponse> {
//        val command = AccountMapper.toRegisterUserAccountCommand(registerUserAccountRequest, actorData)
//        return registerAccountService.execute(command)
//            .map { AccountMapper.toAccountResponse(it) }
//    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody authenticateAccountRequest: AuthenticateAccountRequest): Mono<TokenResponses> {
        val command = AccountMapper.toAuthenticateAccountCommand(authenticateAccountRequest)
        return authenticateAccountService.execute(command)
            .map { SessionMapper.toTokenResponses(it) }
    }

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: String): Mono<AccountResponse> {
        val query = GetAccountQuery(AccountId(accountId))
        return getAccountService.execute(query).map { AccountMapper.toAccountResponse(it) }
    }

//    @PatchMapping("/principal/{principalId}/credentials/rotate")
//    fun rotateCredentials(@PathVariable principalId: String, actorData: ActorData): Mono<CredentialRotatedResponse> {
//        val command = AccountMapper.toRotateCredentialsCommand(principalId, actorData)
//        return rotateCredentialService.execute(command)
//            .map { CredentialMapper.toCredentialRotatedResponse(it) }
//    }


//
//    @PostMapping("/login/actor")
//    fun actorLogin(
//        @RequestHeader("authorization") token: String,
//        @RequestBody @Validated actorLoginRequest: ActorLoginRequest,
//        userData: UserData
//    ): Mono<TokenResponsesDto> {
//        val actorLoginCommand = AccountMapper.toActorLoginCommand(actorLoginRequest, userData, token)
//        return actorLoginService.actorLogin(actorLoginCommand)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
//
//    @PostMapping("/login/device")
//    fun deviceLogin(
//        @RequestBody @Validated deviceLoginRequest: DeviceLoginRequest,
//    ): Mono<TokenResponsesDto> {
//        val deviceLoginCommand = AuthDeviceMapper.toDeviceLoginCommand(deviceLoginRequest)
//        return authDeviceService.deviceLogin(deviceLoginCommand)
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
//        return refreshTokenService.refresh(refreshTokenCommand)
//            .map { TokenMapper.toTokenResponsesDto(it) }
//    }
//
//    fun registerDevice(@RequestBody @Validated request: AuthDeviceRegisterRequest, invitationData: InvitationData): Mono<AuthDeviceResponse> {
//        val command = AuthDeviceMapper.toAuthDeviceRegisterCommand(request)
//        return authDeviceService.register(command, invitationData)
//            .map { AuthDeviceMapper.toAuthDeviceResponse(it) }
//    }
}
