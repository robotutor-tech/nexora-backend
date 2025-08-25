package com.robotutor.nexora.modules.iam.adapters.model

import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.model.EntitlementId
import com.robotutor.nexora.modules.iam.domain.model.EntitlementStatus
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ENTITLEMENT_COLLECTION = "entitlements"

@TypeAlias("Entitlement")
@Document(ENTITLEMENT_COLLECTION)
data class EntitlementDocument(
    @Id val id: String? = null,
    @Indexed(unique = true)
    val entitlementId: String,
    @Indexed
    val roleId: String,
    @Indexed
    val premisesId: String,
    @Indexed
    val action: ActionType,
    @Indexed
    val resourceType: ResourceType,
    val resourceId: String,
    val status: EntitlementStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long?,
) {
    fun toDomainModel(): Entitlement {
        return Entitlement(
            entitlementId = EntitlementId(entitlementId),
            roleId = RoleId(roleId),
            premisesId = PremisesId(premisesId),
            action = action,
            resourceType = resourceType,
            resourceId = ResourceId(resourceId),
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version,
        )
    }

    companion object {
        fun from(entitlement: Entitlement): EntitlementDocument {
            return EntitlementDocument(
                entitlementId = entitlement.entitlementId.value,
                roleId = entitlement.roleId.value,
                premisesId = entitlement.premisesId.value,
                action = entitlement.action,
                resourceType = entitlement.resourceType,
                resourceId = entitlement.resourceId.value,
                status = entitlement.status,
                createdAt = entitlement.createdAt,
                updatedAt = entitlement.updatedAt,
                version = entitlement.version,
            )
        }
    }
}