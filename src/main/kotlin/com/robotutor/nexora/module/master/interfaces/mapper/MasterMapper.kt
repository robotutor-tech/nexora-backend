package com.robotutor.nexora.module.master.interfaces.mapper

import com.robotutor.nexora.module.master.domain.aggregate.Address
import com.robotutor.nexora.module.master.interfaces.view.AddressResponse

object MasterMapper {
    fun toAddressResponse(address: Address): AddressResponse {
        return AddressResponse(
            pinCode = address.pinCode.value,
            city = address.city.value,
            district = address.district.value,
            state = address.state.value,
            country = address.country.value,
        )
    }
}
