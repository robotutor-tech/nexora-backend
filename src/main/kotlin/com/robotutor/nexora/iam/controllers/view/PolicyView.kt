package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.*
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.Identifier

data class PolicyRequest(
    val permission: Permission,
    val identifier: Identifier<Resource>? = null
)

data class PolicyView(
    val policyId: PolicyId,
    val premisesId: PremisesId,
    val permission: Permission,
    val identifier: Identifier<Resource>?,
) {
    companion object {
        fun from(policy: Policy): PolicyView {
            return PolicyView(
                policyId = policy.policyId,
                premisesId = policy.premisesId,
                permission = policy.permission,
                identifier = policy.identifier,
            )
        }
    }

}

