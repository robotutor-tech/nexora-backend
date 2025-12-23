package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.command.GetActorQuery
import com.robotutor.nexora.context.iam.application.command.GetActorsQuery
import com.robotutor.nexora.context.iam.application.usecase.ActorUseCase
import com.robotutor.nexora.context.iam.application.usecase.AuthenticateActorUseCase
import com.robotutor.nexora.context.iam.application.usecase.RegisterMachineActorUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.SessionMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateActorRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.MachineActorRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/actors")
class ActorController(
    private val actorUseCase: ActorUseCase,
    private val authenticateActorUseCase: AuthenticateActorUseCase,
    private val registerMachineActorUseCase: RegisterMachineActorUseCase
) {
    @GetMapping
    fun getActors(accountData: AccountData): Flux<ActorResponse> {
        val query = GetActorsQuery(accountData.accountId)
        return actorUseCase.execute(query)
            .map { ActorMapper.toActorResponse(it) }
    }

    @GetMapping("/me")
    fun getActor(actorData: ActorData): Mono<ActorResponse> {
        val query = GetActorQuery(actorData.actorId, actorData.premisesId)
        return actorUseCase.execute(query)
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

    @PostMapping("/machine")
    fun registerMachineActor(
        @RequestBody @Validated actorRequest: MachineActorRequest,
        accountData: AccountData
    ): Mono<ActorResponse> {
        val command = ActorMapper.toRegisterMachineActorCommand(actorRequest, accountData)
        return registerMachineActorUseCase.execute(command)
            .map { ActorMapper.toActorResponse(it) }
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
