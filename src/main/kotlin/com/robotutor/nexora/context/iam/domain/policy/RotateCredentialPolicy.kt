package com.robotutor.nexora.context.iam.domain.policy

import com.robotutor.nexora.context.iam.domain.policy.context.RotateCredentialPolicyContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import org.springframework.stereotype.Service

@Service
class RotateCredentialPolicy : Policy<RotateCredentialPolicyContext> {
    override fun evaluate(input: RotateCredentialPolicyContext): PolicyResult {
        val reasons = mutableListOf<String>()
        if (input.account.type !== AccountType.MACHINE) {
            reasons.add("Account is not MACHINE type")
        }
        if (input.account.createdBy != input.actorData.actorId) {
            reasons.add("Account is not created by the actor")
        }
        return PolicyResult.create(reasons)
    }
}