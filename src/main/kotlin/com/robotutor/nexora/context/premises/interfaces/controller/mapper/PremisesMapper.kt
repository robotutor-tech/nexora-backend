package com.robotutor.nexora.context.premises.interfaces.controller.mapper

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.context.premises.application.command.RegisterPremisesCommand
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.context.premises.interfaces.controller.dto.*
import com.robotutor.nexora.shared.domain.vo.Name

object PremisesMapper {
    fun toRegisterPremisesCommand(
        premisesRequest: PremisesCreateRequest,
        accountData: AccountData
    ): RegisterPremisesCommand {
        return RegisterPremisesCommand(
            name = Name(premisesRequest.name),
            address = toAddress(premisesRequest.address),
            owner = accountData
        )
    }

    fun toPremisesResponse(premisesAggregate: PremisesAggregate): PremisesResponse {
        return PremisesResponse(
            premisesId = premisesAggregate.premisesId.value,
            name = premisesAggregate.name.value,
            address = toAddressResponse(premisesAggregate.address),
            createdAt = premisesAggregate.createdAt,
            state = premisesAggregate.getState(),
            updatedAt = premisesAggregate.getUpdatedAt()
        )
    }

    private fun toAddressResponse(address: Address): AddressResponse {
        return AddressResponse(
            street = address.street,
            city = address.city,
            state = address.state,
            country = address.country,
            postalCode = address.postalCode
        )
    }

    private fun toAddress(addressRequest: AddressRequest): Address {
        return Address(
            street = addressRequest.street,
            city = addressRequest.city,
            state = addressRequest.state,
            country = addressRequest.country,
            postalCode = addressRequest.postalCode
        )
    }
}