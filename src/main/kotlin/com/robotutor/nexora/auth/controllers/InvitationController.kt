package com.robotutor.nexora.auth.controllers

import com.robotutor.nexora.auth.controllers.views.*
import com.robotutor.nexora.auth.services.InvitationService
import com.robotutor.nexora.auth.services.TokenService
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth/invitations")
class InvitationController(private val invitationService: InvitationService, private val tokenService: TokenService) {

    @PostMapping("/devices")
    fun deviceInvitation(
        @RequestBody @Validated deviceInvitationRequest: DeviceInvitationRequest,
        userData: PremisesActorData
    ): Mono<InvitationView> {
        return invitationService.crateDeviceInvitation(deviceInvitationRequest, userData)
            .flatMap { invitation ->
                tokenService.generateInvitationToken(invitation, userData)
                    .map { token -> InvitationView.from(token, invitation) }
            }
    }

    @GetMapping("/devices")
    fun getDeviceInvitation(userData: PremisesActorData): Flux<InvitationView> {
        return invitationService.getInvitation(userData)
            .flatMap { invitation ->
                tokenService.getInvitationToken(invitation)
                    .map { token -> InvitationView.from(token, invitation) }
            }
    }

    @PostMapping("/users")
    fun userInvitation(
        @RequestBody @Validated userInvitationRequest: UserInvitationRequest,
        userData: PremisesActorData
    ): Mono<InvitationView> {
        return invitationService.crateUserInvitation(userInvitationRequest, userData)
            .flatMap { invitation ->
                tokenService.generateInvitationToken(invitation, userData)
                    .map { token -> InvitationView.from(token, invitation) }
            }
    }

    @GetMapping("/users")
    fun getUserInvitation(userData: PremisesActorData): Flux<InvitationView> {
        return invitationService.getInvitation(userData)
            .flatMap { invitation ->
                tokenService.getInvitationToken(invitation)
                    .map { token -> InvitationView.from(token, invitation) }
            }
    }
}
