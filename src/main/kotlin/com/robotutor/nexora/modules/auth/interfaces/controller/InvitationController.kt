package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.modules.auth.application.InvitationUseCase
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationResponse
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.InvitationWithTokenResponse
import com.robotutor.nexora.modules.auth.interfaces.controller.mapper.InvitationMapper
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth/invitations")
class InvitationController(
    private val invitationUseCase: InvitationUseCase,
) {

    @RequireAccess(ActionType.CREATE, ResourceType.INVITATION)
    @PostMapping
    fun createInvitation(
        @RequestBody @Validated invitationRequest: InvitationRequest,
        actorData: ActorData
    ): Mono<InvitationWithTokenResponse> {
        val invitationCommand = InvitationMapper.toInvitationCommand(invitationRequest)
        return invitationUseCase.createInvitation(invitationCommand, actorData)
            .map { pair -> InvitationMapper.toInvitationWithTokenResponse(pair) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.INVITATION)
    @GetMapping
    fun getDeviceInvitations(resourcesData: ResourcesData): Flux<InvitationWithTokenResponse> {
        val invitationIds = resourcesData.getResourceIds(ActionType.LIST, ResourceType.INVITATION)
            .map { InvitationId(it) }
        return invitationUseCase.getInvitations(invitationIds)
            .map { pair -> InvitationMapper.toInvitationWithTokenResponse(pair) }
    }

    @RequireAccess(ActionType.READ, ResourceType.INVITATION, "invitationId")
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
