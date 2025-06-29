package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.RoleEntitlementRequest
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

val INFINITY_TIME: Instant = Instant.parse("9999-12-31T00:00:00.00Z")
const val ROLE_ENTITLEMENT_COLLECTION = "roleEntitlements"

@TypeAlias("RoleEntitlement")
@Document(ROLE_ENTITLEMENT_COLLECTION)
data class RoleEntitlement(
    @Id val id: String? = null,
    @Indexed(unique = true)
    val roleEntitlementId: RoleEntitlementId,
    val roleId: RoleId,
    val entitlementId: EntitlementId,
    val resourceId: String = "*",
    val premisesId: PremisesId,
    val status: EntitlementStatus = EntitlementStatus.ACTIVE,
    val policyId: PolicyId? = null,
    val createdAt: Instant = Instant.now(),
    val expiresAt: Instant = INFINITY_TIME
) {
    companion object {
        fun from(
            roleEntitlementId: RoleEntitlementId,
            premisesId: PremisesId,
            request: RoleEntitlementRequest
        ): RoleEntitlement {
            return RoleEntitlement(
                roleEntitlementId = roleEntitlementId,
                roleId = request.roleId,
                entitlementId = request.entitlementId,
                resourceId = request.resourceId,
                premisesId = premisesId,
            )
        }
    }
}

enum class EntitlementStatus {
    DRAFT,
    ACTIVE,
    REVOKED,
    EXPIRED
}

typealias RoleEntitlementId = String