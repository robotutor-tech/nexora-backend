package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ROLE_COLLECTION = "roles"

@Document(ROLE_COLLECTION)
@TypeAlias("Role")
data class RoleDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val roleId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val permissions: Set<PermissionDocument>,
    val type: RoleType,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null,
) : MongoDocument<RoleAggregate>

data class PermissionDocument(
    val resourceType: ResourceType,
    val action: ActionType,
    val resource: String,
)