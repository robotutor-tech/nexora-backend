package com.robotutor.nexora.module.master.infrastructure.persistence.mapper

import com.robotutor.nexora.module.master.domain.aggregate.Address
import com.robotutor.nexora.module.master.domain.vo.City
import com.robotutor.nexora.module.master.domain.vo.Country
import com.robotutor.nexora.module.master.domain.vo.District
import com.robotutor.nexora.module.master.domain.vo.PinCode
import com.robotutor.nexora.module.master.domain.vo.State
import com.robotutor.nexora.module.master.infrastructure.persistence.document.AddressDocument
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object AddressDocumentMapper : DocumentMapper<Address, AddressDocument> {

    override fun toDocument(domain: Address): AddressDocument {
        return AddressDocument(
            id = domain.getObjectId(),
            pinCode = domain.pinCode.value,
            city = domain.city.value,
            district = domain.district.value,
            state = domain.state.value,
            country = domain.country.value,
            version = domain.getVersion(),
        )
    }

    override fun toDomain(document: AddressDocument): Address {
        return Address(
            pinCode = PinCode(document.pinCode),
            city = City(document.city),
            district = District(document.district),
            state = State(document.state),
            country = Country(document.country),
        )
            .setObjectIdAndVersion(document.id, document.version)
    }
}
