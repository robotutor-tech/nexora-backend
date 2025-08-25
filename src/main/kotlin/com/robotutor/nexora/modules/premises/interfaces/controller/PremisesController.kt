package com.robotutor.nexora.modules.premises.interfaces.controller

import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.modules.premises.application.PremisesUseCase
import com.robotutor.nexora.modules.premises.application.command.CreatePremisesCommand
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.PremisesActorResponse
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.PremisesCreateRequest
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.PremisesResponse
import com.robotutor.nexora.modules.premises.interfaces.controller.mapper.PremisesMapper
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/premises")
class PremisesController(private val premisesUseCase: PremisesUseCase) {

    @PostMapping
    fun createPremises(
        @RequestBody @Validated premisesRequest: PremisesCreateRequest,
        userData: UserData
    ): Mono<PremisesActorResponse> {
        val createPremisesCommand = CreatePremisesCommand(Name(premisesRequest.name), userData)
        return premisesUseCase.createPremises(createPremisesCommand)
            .map { PremisesMapper.toPremisesActorResponse(it) }
    }

    @GetMapping
    fun getPremises(userData: UserData): Flux<PremisesActorResponse> {
        return premisesUseCase.getAllPremises(userData)
            .map { PremisesMapper.toPremisesActorResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.PREMISES, "premisesId")
    @GetMapping("/{premisesId}")
    fun getPremisesDetails(@PathVariable premisesId: String): Mono<PremisesResponse> {
        return premisesUseCase.getPremisesDetails(PremisesId(premisesId))
            .map { premises -> PremisesMapper.toPremisesResponse(premises) }
    }
}