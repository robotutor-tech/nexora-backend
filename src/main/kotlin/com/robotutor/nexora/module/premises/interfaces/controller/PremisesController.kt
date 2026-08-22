package com.robotutor.nexora.module.premises.interfaces.controller

import com.robotutor.nexora.module.premises.application.command.GetPremisesQuery
import com.robotutor.nexora.module.premises.application.service.GetPremisesService
import com.robotutor.nexora.module.premises.application.service.RegisterPremisesService
import com.robotutor.nexora.module.premises.interfaces.controller.mapper.PremisesMapper
import com.robotutor.nexora.module.premises.interfaces.controller.view.PremisesCreateRequest
import com.robotutor.nexora.module.premises.interfaces.controller.view.PremisesResponse
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.UserData
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
    fun register(@RequestBody request: PremisesCreateRequest, userData: UserData): Mono<PremisesResponse> {
        val command = PremisesMapper.toRegisterPremisesCommand(request, userData)
        return registerPremisesService.execute(command)
            .map { PremisesMapper.toPremisesResponse(it) }
    }

    @GetMapping
    fun getAllPremises(principalData: PrincipalData): Flux<PremisesResponse> {
        return getPremisesService.execute(principalData)
            .map { PremisesMapper.toPremisesResponse(it) }
    }

    @GetMapping("/{premisesId}")
    fun getPremisesDetails(@PathVariable premisesId: String): Mono<PremisesResponse> {
        val query = GetPremisesQuery(PremisesId(premisesId))
        return getPremisesService.execute(query)
            .map { premises -> PremisesMapper.toPremisesResponse(premises) }
    }
}
