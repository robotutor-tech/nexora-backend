package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.modules.auth.application.InvitationUseCase
import com.robotutor.nexora.modules.auth.application.TokenUseCase
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationResponse
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationWithTokenResponse
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.InvitationMapper
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InvitationId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth/invitations")
class InvitationController(
    private val invitationUseCase: InvitationUseCase,
    private val tokenUseCase: TokenUseCase
) {

    @PostMapping
    fun createInvitation(
        @RequestBody @Validated invitationRequest: InvitationRequest,
        actorData: ActorData
    ): Mono<InvitationWithTokenResponse> {
        val invitationCommand = InvitationMapper.toInvitationCommand(invitationRequest)
        return invitationUseCase.createInvitation(invitationCommand, actorData)
            .map { pair -> InvitationMapper.toInvitationWithTokenResponse(pair) }
    }


    @GetMapping
    fun getDInvitations(actorData: ActorData): Flux<InvitationWithTokenResponse> {
        return invitationUseCase.getInvitations(actorData)
            .map { pair -> InvitationMapper.toInvitationWithTokenResponse(pair) }
    }

    @GetMapping("/{invitationId}")
    fun getInvitation(@PathVariable invitationId: String): Mono<InvitationResponse> {
        return invitationUseCase.getInvitation(InvitationId(invitationId))
            .map { InvitationMapper.toInvitationResponse(it) }
    }

//    @PostMapping("/users")
//    fun userInvitation(
//        @RequestBody @Validated userInvitationRequest: UserInvitationRequest,
//        userData: PremisesActorData
//    ): Mono<UserInvitationView> {
//        return invitationService.crateUserInvitation(userInvitationRequest, userData)
//            .map { invitation -> UserInvitationView.Companion.from(invitation) }
//    }
}
