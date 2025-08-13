package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.modules.auth.application.AuthUserService
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserResponseDto
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.AuthUserMapper
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(private val authUserService: AuthUserService) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated authUserRequest: AuthUserRequest): Mono<AuthUserResponseDto> {
        val registerAuthUserCommand = AuthUserMapper.toRegisterAuthUserCommand(authUserRequest)
        return authUserService.register(registerAuthUserCommand)
            .map { AuthUserMapper.toAuthUserResponseDto(it) }
    }

//    @PostMapping("/login")
//    fun login(@RequestBody @Validated authLoginRequest: AuthLoginRequest): Mono<TokenView> {
//        val loginCommand = AuthUserMapper.toLoginCommand(authLoginRequest)
//        return authUserService.login(loginCommand)
////            .flatMap { tokenService.generateAuthUserToken(it) }
////            .map { TokenView.Companion.from(it) }
//    }

//    @GetMapping("/validate")
//    fun validate(): Mono<IAuthenticationData> {
//        return Mono.deferContextual { ctx ->
//            val premisesActorDataOptional = ctx.getOrEmpty<PremisesActorData>(PremisesActorData::class.java)
//            val authUserDataOptional = ctx.getOrEmpty<AuthUserData>(AuthUserData::class.java)
//            val invitationDataOptional = ctx.getOrEmpty<InvitationData>(InvitationData::class.java)
//            when {
//                premisesActorDataOptional.isPresent -> createMono(premisesActorDataOptional.get())
//                    .map { PremisesActorDataView.Companion.from(it) }
//
//                authUserDataOptional.isPresent -> createMono(authUserDataOptional.get())
//                invitationDataOptional.isPresent -> createMono(invitationDataOptional.get())
//                else -> createMonoError(UnAuthorizedException(NexoraError.NEXORA0203))
//            }
//        }
//    }
}
