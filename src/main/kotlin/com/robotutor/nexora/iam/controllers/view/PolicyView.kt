package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.*
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.mongodb.core.index.Indexed

data class PolicyRequest(
    @field:NotBlank(message = "Premises Id is required")
    val premisesId: PremisesId,
    @field:NotBlank(message = "Name is required")
    val name: String,
    val type: PolicyType,
    @field:NotBlank(message = "Feed is required")
    val feedId: String,
    val access: AccessType,
)

data class PolicyView(
    val policyId: PolicyId,
    val premisesId: PremisesId,
    val name: String,
    val type: PolicyType,
    val feedId: String,
    val access: AccessType,
) {
    companion object {
        fun from(policy: Policy): PolicyView {
            return PolicyView(
                policyId = policy.policyId,
                premisesId = policy.premisesId,
                name = policy.name,
                type = policy.type,
                feedId = policy.feedId,
                access = policy.access
            )
        }
    }

}

