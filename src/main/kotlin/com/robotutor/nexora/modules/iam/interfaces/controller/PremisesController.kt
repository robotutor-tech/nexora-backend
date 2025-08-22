package com.robotutor.nexora.modules.iam.interfaces.controller

import com.robotutor.nexora.modules.iam.application.RegisterPremisesResourceUseCase
import com.robotutor.nexora.modules.iam.application.RoleUseCase
import com.robotutor.nexora.modules.iam.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorWithRolesResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.PremisesRequest
import com.robotutor.nexora.modules.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController(value = "IAMPremisesController")
@RequestMapping("/iam/premises")
class PremisesController(
    private val registerPremisesResourceUseCase: RegisterPremisesResourceUseCase,
    private val roleUseCase: RoleUseCase,
) {

    @PostMapping("/register")
    fun registerPremises(
        @RequestBody @Validated request: PremisesRequest,
        userData: UserData
    ): Mono<ActorWithRolesResponse> {
        val registerPremisesResourceCommand = RegisterPremisesResourceCommand(PremisesId(request.premisesId), userData)
        return registerPremisesResourceUseCase.registerPremises(registerPremisesResourceCommand)
            .flatMap { actor ->
                roleUseCase.getRolesByRoleIds(actor.roleIds).collectList()
                    .map { roles -> ActorMapper.toActorWithRolesResponse(actor, roles) }
            }
    }

//    @PostMapping("/register/device")
//    fun registerDevice(
//        @RequestBody @Validated request: RegisterDeviceRequest,
//        invitationData: InvitationData
//    ): Mono<ActorView> {
//        return premisesService.registerDevice(request, invitationData)
//            .flatMap { actor ->
//                roleService.getRoleByRoleId(actor.roles.first())
//                    .map { ActorView.from(actor, it) }
//            }
//    }
}