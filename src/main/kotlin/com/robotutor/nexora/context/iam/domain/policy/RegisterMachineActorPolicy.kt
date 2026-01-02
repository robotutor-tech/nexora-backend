package com.robotutor.nexora.context.iam.domain.policy

import com.robotutor.nexora.context.iam.domain.policy.context.RegisterMachineActorPolicyContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import org.springframework.stereotype.Service

@Service
class RegisterMachineActorPolicy : Policy<RegisterMachineActorPolicyContext> {
    override fun evaluate(input: RegisterMachineActorPolicyContext): PolicyResult {
        val reasons = mutableListOf<String>()
        if (input.owner.type != AccountType.MACHINE)
            reasons.add("Account is not MACHINE type")

        if (input.actorAlreadyExists) {
            reasons.add("Actor already exists")
        }

        return if (reasons.isEmpty()) PolicyResult.allow()
        else PolicyResult.deny(reasons)
    }
}