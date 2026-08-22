package com.robotutor.nexora.module.master.application.service

import com.robotutor.nexora.module.master.domain.aggregate.Address
import com.robotutor.nexora.module.master.domain.exception.MasterError
import com.robotutor.nexora.module.master.domain.repository.AddressRepository
import com.robotutor.nexora.module.master.domain.vo.PinCode
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GetAddressService(private val addressRepository: AddressRepository) {

    fun execute(pinCode: PinCode): Mono<Address> {
        return addressRepository.find(pinCode)
            .required(DataNotFoundException(MasterError.NEXORA0401))
    }
}
