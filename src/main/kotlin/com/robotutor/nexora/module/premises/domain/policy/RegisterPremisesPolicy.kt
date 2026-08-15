package com.robotutor.nexora.module.premises.domain.policy

import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import org.springframework.stereotype.Service

@Service
class RegisterPremisesPolicy : Policy<PrincipalData> {
    override fun evaluate(input: PrincipalData): PolicyResult {
        val reasons = mutableListOf<String>()

        return PolicyResult.create(reasons)
    }
}
