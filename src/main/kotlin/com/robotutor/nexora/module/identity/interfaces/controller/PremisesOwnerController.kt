package com.robotutor.nexora.module.identity.interfaces.controller

import com.robotutor.nexora.module.identity.application.service.RegisterOwnerService
import com.robotutor.nexora.module.identity.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.module.identity.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.module.identity.interfaces.controller.view.OwnerCreationRequest
import com.robotutor.nexora.shared.domain.vo.UserData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/identity/premises-owners")
class PremisesOwnerController(private val registerOwnerService: RegisterOwnerService) {

    @PostMapping("/register")
    fun registerPremisesResource(
        @RequestBody eventMessage: OwnerCreationRequest,
        userData: UserData,
    ): Mono<ActorResponse> {
        val command = ActorMapper.toRegisterOwnerCommand(eventMessage, userData)
        return registerOwnerService.execute(command)
            .map { ActorMapper.toActorResponse(it) }
    }
}
