package com.robotutor.nexora.iam.models

import com.robotutor.nexora.premises.models.PremisesId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


const val POLICY_COLLECTION = "policies"

@TypeAlias("Policy")
@Document(POLICY_COLLECTION)
data class Policy(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val policyId: PolicyId,
    @Indexed
    val premisesId: PremisesId,
    val name: String,
    val type: PolicyType,
    val feedId: String,
    val permission: PermissionType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

enum class PolicyType {
    GLOBAL,
    LOCAL
}

enum class PermissionType {
    READ,
    UPDATE
}

typealias PolicyId = String