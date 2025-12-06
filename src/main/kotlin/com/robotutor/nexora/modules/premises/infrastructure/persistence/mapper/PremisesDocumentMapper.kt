package com.robotutor.nexora.modules.premises.infrastructure.persistence.mapper

import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.modules.premises.domain.entity.Address
import com.robotutor.nexora.modules.premises.domain.entity.Premises
import com.robotutor.nexora.modules.premises.infrastructure.persistence.document.PremisesDocument
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object PremisesDocumentMapper : DocumentMapper<Premises, PremisesDocument> {
    override fun toMongoDocument(domain: Premises): PremisesDocument {
        return PremisesDocument(
            id = null, // Let MongoDB generate the ObjectId
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            street = domain.address.street,
            city = domain.address.city,
            state = domain.address.state,
            country = domain.address.country,
            postalCode = domain.address.postalCode,
            owner = domain.owner.value,
            createdAt = domain.createdAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: PremisesDocument): Premises {
        return Premises(
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            address = Address(
                street = document.street,
                city = document.city,
                state = document.state,
                country = document.country,
                postalCode = document.postalCode
            ),
            owner = UserId(document.owner),
            createdAt = document.createdAt,
            version = document.version
        )
    }
}