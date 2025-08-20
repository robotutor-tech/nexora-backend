package com.robotutor.nexora.modules.auth.interfaces.controller

import com.robotutor.nexora.modules.auth.services.InvitationService
import com.robotutor.nexora.modules.auth.services.TokenService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/invitations")
class InvitationController(private val invitationService: InvitationService, private val tokenService: TokenService) {

//    @PostMapping("/devices")
//    fun deviceInvitation(
//        @RequestBody @Validated deviceInvitationRequest: DeviceInvitationRequest,
//        premisesActorData: PremisesActorData
//    ): Mono<DeviceInvitationView> {
//        return invitationService.crateDeviceInvitation(deviceInvitationRequest, premisesActorData)
//            .flatMap { invitation ->
//                tokenService.generateInvitationToken(invitation)
//                    .map { token -> DeviceInvitationView.Companion.from(invitation, token) }
//            }
//    }
//
//    @GetMapping("/devices")
//    fun getDeviceInvitations(premisesActorData: PremisesActorData): Flux<DeviceInvitationView> {
//        return invitationService.getDeviceInvitations(premisesActorData)
//            .flatMap { invitation ->
//                tokenService.getInvitationToken(invitation)
//                    .map { token -> DeviceInvitationView.Companion.from(invitation, token) }
//            }
//    }
//
//    @GetMapping("/{invitationId}/devices")
//    fun getDeviceInvitation(@PathVariable invitationId: InvitationId): Mono<DeviceInvitationView> {
//        return invitationService.getDeviceInvitation(invitationId)
//            .map { DeviceInvitationView.Companion.from(it, null) }
//    }
//
//    @PostMapping("/users")
//    fun userInvitation(
//        @RequestBody @Validated userInvitationRequest: UserInvitationRequest,
//        userData: PremisesActorData
//    ): Mono<UserInvitationView> {
//        return invitationService.crateUserInvitation(userInvitationRequest, userData)
//            .map { invitation -> UserInvitationView.Companion.from(invitation) }
//    }
}
