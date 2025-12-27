package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.usecase.RegisterOwnerUseCase
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.ActorMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.OwnerCreationRequest
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/premises-owners")
class PremisesOwnerController(private val registerOwnerUseCase: RegisterOwnerUseCase) {

    @PostMapping("/register")
    fun registerPremisesResource(
        @RequestBody eventMessage: OwnerCreationRequest,
        AccountData: AccountData
    ): Mono<ActorResponse> {
        val command = ActorMapper.toRegisterOwnerCommand(eventMessage, AccountData)
        return registerOwnerUseCase.execute(command)
            .map { ActorMapper.toActorResponse(it) }
    }
}