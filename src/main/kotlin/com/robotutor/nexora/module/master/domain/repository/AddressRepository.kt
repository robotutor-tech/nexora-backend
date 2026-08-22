package com.robotutor.nexora.module.master.domain.repository

import com.robotutor.nexora.module.master.domain.aggregate.Address
import com.robotutor.nexora.module.master.domain.vo.PinCode
import reactor.core.publisher.Mono

interface AddressRepository {
    fun save(address: Address): Mono<Address>
    fun find(pinCode: PinCode): Mono<Address>
}
