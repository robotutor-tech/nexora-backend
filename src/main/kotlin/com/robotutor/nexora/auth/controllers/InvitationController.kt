package com.robotutor.nexora.auth.controllers

import com.robotutor.nexora.auth.controllers.views.DeviceInvitationRequest
import com.robotutor.nexora.auth.controllers.views.DeviceInvitationView
import com.robotutor.nexora.auth.controllers.views.UserInvitationRequest
import com.robotutor.nexora.auth.controllers.views.UserInvitationView
import com.robotutor.nexora.auth.models.InvitationId
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
        premisesActorData: PremisesActorData
    ): Mono<DeviceInvitationView> {
        return invitationService.crateDeviceInvitation(deviceInvitationRequest, premisesActorData)
            .flatMap { invitation ->
                tokenService.generateInvitationToken(invitation)
                    .map { token -> DeviceInvitationView.from(invitation, token) }
            }
    }

    @GetMapping("/devices")
    fun getDeviceInvitations(premisesActorData: PremisesActorData): Flux<DeviceInvitationView> {
        return invitationService.getDeviceInvitations(premisesActorData)
            .flatMap { invitation ->
                tokenService.getInvitationToken(invitation)
                    .map { token -> DeviceInvitationView.from(invitation, token) }
            }
    }

    @GetMapping("/{invitationId}/devices")
    fun getDeviceInvitation(@PathVariable invitationId: InvitationId): Mono<DeviceInvitationView> {
        return invitationService.getDeviceInvitation(invitationId)
            .map { DeviceInvitationView.from(it, null) }
    }

    @PostMapping("/users")
    fun userInvitation(
        @RequestBody @Validated userInvitationRequest: UserInvitationRequest,
        userData: PremisesActorData
    ): Mono<UserInvitationView> {
        return invitationService.crateUserInvitation(userInvitationRequest, userData)
            .map { invitation -> UserInvitationView.from(invitation) }
    }
}
