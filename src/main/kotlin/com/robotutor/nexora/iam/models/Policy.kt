package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.PolicyRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
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
    val access: AccessType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(policyId: String, request: PolicyRequest, premisesActorData: PremisesActorData): Policy {
            return Policy(
                policyId = policyId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                type = request.type,
                feedId = request.feedId,
                access = request.access
            )
        }
    }
}

enum class PolicyType {
    GLOBAL,
    LOCAL
}

enum class AccessType {
    READ,
    UPDATE
}

typealias PolicyId = String