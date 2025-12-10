package com.robotutor.nexora.modules.iam.infrastructure.persistence.document

import com.robotutor.nexora.modules.iam.domain.entity.Role
import com.robotutor.nexora.shared.domain.model.RoleType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ROLE_COLLECTION = "roles"

@TypeAlias("Role")
@Document(ROLE_COLLECTION)
data class RoleDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val roleId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val roleType: RoleType,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long
) : MongoDocument<Role>