package com.robotutor.nexora.context.premises.interfaces.controller

import com.robotutor.nexora.context.premises.application.command.GetAllPremisesQuery
import com.robotutor.nexora.context.premises.application.command.GetPremisesQuery
import com.robotutor.nexora.context.premises.application.service.GetPremisesService
import com.robotutor.nexora.context.premises.application.service.RegisterPremisesService
import com.robotutor.nexora.context.premises.interfaces.controller.mapper.PremisesMapper
import com.robotutor.nexora.context.premises.interfaces.controller.view.PremisesCreateRequest
import com.robotutor.nexora.context.premises.interfaces.controller.view.PremisesResponse
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/premises")
class PremisesController(
    private val registerPremisesService: RegisterPremisesService,
    private val getPremisesService: GetPremisesService
) {
    @PostMapping
    fun registerPremises(
        @RequestBody @Validated premisesRequest: PremisesCreateRequest,
        accountData: AccountData
    ): Mono<PremisesResponse> {
        val command = PremisesMapper.toRegisterPremisesCommand(premisesRequest, accountData)
        return registerPremisesService.execute(command)
            .map { PremisesMapper.toPremisesResponse(it) }
    }

    @GetMapping
    fun getAllPremises(@RequestParam premisesIds: List<String>): Flux<PremisesResponse> {
        val query = GetAllPremisesQuery(premisesIds.map { PremisesId(it) })
        return getPremisesService.execute(query)
            .map { PremisesMapper.toPremisesResponse(it) }
    }

    @GetMapping("/{premisesId}")
    fun getPremisesDetails(@PathVariable premisesId: String): Mono<PremisesResponse> {
        val query = GetPremisesQuery(PremisesId(premisesId))
        return getPremisesService.execute(query)
            .map { premises -> PremisesMapper.toPremisesResponse(premises) }
    }
}