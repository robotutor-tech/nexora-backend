package com.robotutor.nexora.iam.models

import com.robotutor.nexora.premises.models.PremisesId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.ZoneOffset

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
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
)

typealias PolicyId = String