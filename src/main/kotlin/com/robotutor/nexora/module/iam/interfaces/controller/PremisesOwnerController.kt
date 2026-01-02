package com.robotutor.nexora.module.iam.interfaces.controller

import com.robotutor.nexora.module.iam.application.service.RegisterOwnerService
import com.robotutor.nexora.module.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.module.iam.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.module.iam.interfaces.controller.view.OwnerCreationRequest
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/premises-owners")
class PremisesOwnerController(private val registerOwnerService: RegisterOwnerService) {

    @PostMapping("/register")
    fun registerPremisesResource(
        @RequestBody eventMessage: OwnerCreationRequest,
        accountData: AccountData
    ): Mono<ActorResponse> {
        val command = ActorMapper.toRegisterOwnerCommand(eventMessage, accountData)
        return registerOwnerService.execute(command)
            .map { ActorMapper.toActorResponse(it) }
    }
}