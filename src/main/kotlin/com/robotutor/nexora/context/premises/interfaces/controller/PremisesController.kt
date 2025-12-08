package com.robotutor.nexora.context.premises.interfaces.controller

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.premises.application.command.GetPremisesQuery
import com.robotutor.nexora.context.premises.application.usecase.GetPremisesUseCase
import com.robotutor.nexora.context.premises.application.usecase.RegisterPremisesUseCase
import com.robotutor.nexora.context.premises.interfaces.controller.dto.PremisesCreateRequest
import com.robotutor.nexora.context.premises.interfaces.controller.dto.PremisesResponse
import com.robotutor.nexora.context.premises.interfaces.controller.mapper.PremisesMapper
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/premises")
class PremisesController(
    private val registerPremisesUseCase: RegisterPremisesUseCase,
    private val getPremisesUseCase: GetPremisesUseCase
) {

    @PostMapping
    fun registerPremises(
        @RequestBody @Validated premisesRequest: PremisesCreateRequest,
        accountData: AccountData
    ): Mono<PremisesResponse> {
        val command = PremisesMapper.toRegisterPremisesCommand(premisesRequest, accountData)
        return registerPremisesUseCase.execute(command)
            .map { PremisesMapper.toPremisesResponse(it) }
    }

    @GetMapping
    fun getPremises(@RequestParam premisesIds: List<String>): Flux<PremisesResponse> {
        val query = GetPremisesQuery(premisesIds.map { PremisesId(it) })
        return getPremisesUseCase.execute(query)
            .map { PremisesMapper.toPremisesResponse(it) }
    }

//    @RequireAccess(ActionType.READ, ResourceType.PREMISES, "premisesId")
//    @GetMapping("/{premisesId}")
//    fun getPremisesDetails(@PathVariable premisesId: String): Mono<PremisesResponse> {
//        return premisesUseCase.getPremisesDetails(PremisesId(premisesId))
//            .map { premises -> PremisesMapper.toPremisesResponse(premises) }
//    }
}