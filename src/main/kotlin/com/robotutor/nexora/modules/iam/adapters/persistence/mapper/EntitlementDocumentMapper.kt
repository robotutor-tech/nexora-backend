package com.robotutor.nexora.modules.iam.adapters.persistence.mapper

import com.robotutor.nexora.modules.iam.adapters.persistence.model.EntitlementDocument
import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.model.EntitlementId
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.RoleId
import org.springframework.stereotype.Component

@Component
class EntitlementDocumentMapper : DocumentMapper<Entitlement, EntitlementDocument> {
    override fun toMongoDocument(domain: Entitlement): EntitlementDocument = EntitlementDocument(
        id = null, // Let MongoDB handle ObjectId
        entitlementId = domain.entitlementId.value,
        roleId = domain.roleId.value,
        premisesId = domain.premisesId.value,
        action = domain.action,
        resourceType = domain.resourceType,
        resourceId = domain.resourceId.value,
        status = domain.status,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
        version = domain.version
    )

    override fun toDomainModel(document: EntitlementDocument): Entitlement = Entitlement(
        entitlementId = EntitlementId(document.entitlementId),
        roleId = RoleId(document.roleId),
        premisesId = PremisesId(document.premisesId),
        action = document.action,
        resourceType = document.resourceType,
        resourceId = ResourceId(document.resourceId),
        status = document.status,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt,
        version = document.version
    )
}
