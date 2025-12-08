package com.robotutor.nexora.context.premises.infrastructure.persistence.mapper

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.AddressDocument
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.RegisteredByDocument
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.PremisesDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object PremisesDocumentMapper : DocumentMapper<PremisesAggregate, PremisesDocument> {
    override fun toMongoDocument(domain: PremisesAggregate): PremisesDocument {
        return PremisesDocument(
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            address = toAddressDocument(domain.address),
            registeredBy = toCreatedByDocument(domain.registeredBy),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: PremisesDocument): PremisesAggregate {
        return PremisesAggregate(
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            address = toAddress(document.address),
            registeredBy = toCreatedBy(document.registeredBy),
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
            version = document.version
        )
    }

    private fun toCreatedBy(createdBy: RegisteredByDocument): AccountData {
        return AccountData(AccountId(createdBy.accountId), createdBy.type)
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

    private fun toCreatedByDocument(createdBy: AccountData): RegisteredByDocument {
        return RegisteredByDocument(accountId = createdBy.accountId.value, type = createdBy.type)
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