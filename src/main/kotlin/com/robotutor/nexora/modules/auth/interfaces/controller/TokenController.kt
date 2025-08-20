package com.robotutor.nexora.modules.auth.interfaces.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/tokens")
class TokenController(
) {

    //    @PostMapping
//    fun generatePremisesActorToken(
//        @RequestBody @Validated premisesActorRequest: PremisesActorRequest,
//        @RequestHeader("authorization") token: String = "",
//    ): Mono<TokenView> {
//        return tokenService.generatePremisesActorToken(token.removePrefix("Bearer").trim(), premisesActorRequest)
//            .map { TokenView.Companion.from(it) }
//    }
//
//    @PostMapping("/device")
//    fun generateDevicePremisesActorToken(
//        @RequestBody @Validated premisesActorRequest: PremisesActorRequest,
//        invitationData: InvitationData
//    ): Mono<TokenView> {
//        return tokenService.generateDevicePremisesActorToken(premisesActorRequest, invitationData)
//            .flatMap { token ->
//                invitationService.markAsAccepted(invitationData).map { TokenView.Companion.from(token) }
//            }
//    }


}
