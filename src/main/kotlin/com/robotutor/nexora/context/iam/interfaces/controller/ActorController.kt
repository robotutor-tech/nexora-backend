package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.iam.application.command.GetActorsQuery
import com.robotutor.nexora.context.iam.application.usecase.AuthenticateActorUseCase
import com.robotutor.nexora.context.iam.application.usecase.GetActorsUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.SessionMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateActorRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import com.robotutor.nexora.shared.domain.vo.AccountId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/actors")
class ActorController(
    private val getActorsUseCase: GetActorsUseCase,
    private val authenticateActorUseCase: AuthenticateActorUseCase
) {


    @GetMapping
    fun getActors(@RequestParam accountId: String): Flux<ActorResponse> {
        val query = GetActorsQuery(AccountId(accountId))
        return getActorsUseCase.execute(query)
            .map { ActorMapper.toActorResponse(it) }
    }

    @PostMapping("/authenticate")
    fun authenticateActor(
        @RequestHeader("authorization") token: String,
        @RequestBody @Validated authenticateActorRequest: AuthenticateActorRequest,
        accountData: AccountData
    ): Mono<TokenResponses> {
        val command = ActorMapper.toAuthenticateActorCommand(authenticateActorRequest, accountData, token)
        return authenticateActorUseCase.execute(command)
            .map { SessionMapper.toTokenResponses(it) }
    }

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
