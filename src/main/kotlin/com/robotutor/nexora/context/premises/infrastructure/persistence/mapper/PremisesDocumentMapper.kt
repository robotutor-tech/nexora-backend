package com.robotutor.nexora.context.premises.infrastructure.persistence.mapper

import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.AddressDocument
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.PremisesDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.common.persistence.mapper.DocumentMapper

object PremisesDocumentMapper : DocumentMapper<PremisesAggregate, PremisesDocument> {
    override fun toMongoDocument(domain: PremisesAggregate): PremisesDocument {
        return PremisesDocument(
            id = domain.getObjectId(),
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            address = toAddressDocument(domain.address),
            ownerId = domain.ownerId.value,
            state = domain.getState(),
            createdAt = domain.createdAt,
            updatedAt = domain.getUpdatedAt(),
            version = domain.getVersion()
        )
    }

    override fun toDomainModel(document: PremisesDocument): PremisesAggregate {
        return PremisesAggregate
            .create(
                premisesId = PremisesId(document.premisesId),
                name = Name(document.name),
                address = toAddress(document.address),
                ownerId = AccountId(document.ownerId),
                state = document.state,
                createdAt = document.createdAt,
                updatedAt = document.updatedAt,
            )
            .setObjectIdAndVersion(document.id, document.version)
    }


    private fun toAddress(address: AddressDocument): Address {
        return Address(
            street = address.street,
            city = address.city,
            state = address.state,
            country = address.country,
            postalCode = address.postalCode
        )
    }

    private fun toAddressDocument(address: Address): AddressDocument {
        return AddressDocument(
            street = address.street,
            city = address.city,
            state = address.state,
            country = address.country,
            postalCode = address.postalCode
        )
    }
}