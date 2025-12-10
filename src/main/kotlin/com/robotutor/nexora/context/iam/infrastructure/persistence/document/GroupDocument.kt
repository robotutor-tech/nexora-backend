package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.GroupType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val GROUP_COLLECTION = "groups"

@Document(GROUP_COLLECTION)
@TypeAlias("Group")
data class GroupDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val groupId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val roleIds: Set<String>,
    val type: GroupType,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long = 0,
) : MongoDocument<GroupAggregate>