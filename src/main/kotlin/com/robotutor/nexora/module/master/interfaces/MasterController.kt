package com.robotutor.nexora.module.master.interfaces

import com.robotutor.nexora.module.master.application.service.GetAddressService
import com.robotutor.nexora.module.master.domain.vo.PinCode
import com.robotutor.nexora.module.master.interfaces.mapper.MasterMapper
import com.robotutor.nexora.module.master.interfaces.view.AddressResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/master")
class MasterController(private val getAddressService: GetAddressService) {

    @GetMapping("/address/{pinCode}")
    fun getAddress(@PathVariable pinCode: String): Mono<AddressResponse> {
        return getAddressService.execute(PinCode(pinCode))
            .map { MasterMapper.toAddressResponse(it) }
    }
}
