package com.robotutor.nexora.context.premises.domain.policy

import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import org.springframework.stereotype.Service

@Service
class RegisterPremisesPolicy : Policy<AccountData> {
    override fun evaluate(input: AccountData): PolicyResult {
        val reasons = mutableListOf<String>()
        if (!input.isHuman()) {
            reasons.add("Only humans can register premises")
        }
        return PolicyResult.create(reasons)
    }
}