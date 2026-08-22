package com.robotutor.nexora.module.premises.interfaces.controller.mapper

import com.robotutor.nexora.module.premises.application.command.RegisterPremisesCommand
import com.robotutor.nexora.module.premises.domain.aggregate.Premises
import com.robotutor.nexora.module.premises.domain.vo.Address
import com.robotutor.nexora.module.premises.domain.vo.PostalCode
import com.robotutor.nexora.module.premises.domain.vo.Street
import com.robotutor.nexora.module.premises.interfaces.controller.view.AddressRequest
import com.robotutor.nexora.module.premises.interfaces.controller.view.AddressResponse
import com.robotutor.nexora.module.premises.interfaces.controller.view.PremisesCreateRequest
import com.robotutor.nexora.module.premises.interfaces.controller.view.PremisesResponse
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.UserData

object PremisesMapper {
    fun toRegisterPremisesCommand(request: PremisesCreateRequest, userData: UserData): RegisterPremisesCommand {
        return RegisterPremisesCommand(
            name = Name.of(request.name),
            address = toAddress(request.address),
            owner = userData
        )
    }

    fun toPremisesResponse(premises: Premises): PremisesResponse {
        return PremisesResponse(
            premisesId = premises.premisesId.value,
            name = premises.name.value,
            address = toAddressResponse(premises.address),
            createdAt = premises.createdAt,
            state = premises.getState(),
            updatedAt = premises.getUpdatedAt()
        )
    }

    private fun toAddressResponse(address: Address): AddressResponse {
        return AddressResponse(
            street = address.street.value,
            postalCode = address.postalCode.value
        )
    }

    private fun toAddress(request: AddressRequest): Address {
        return Address(street = Street.of(request.street), postalCode = PostalCode.of(request.postalCode))
    }
}
