package com.robotutor.nexora.context.zone.domain.policy

import com.robotutor.nexora.context.zone.domain.policy.context.DuplicateZoneNameContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class CreateZonePolicy : Policy<DuplicateZoneNameContext> {
    override fun evaluate(input: DuplicateZoneNameContext): PolicyResult {
        val reasons = mutableListOf<String>()
        if (input.zoneAlreadyExists) {
            reasons.add("Zone with name ${input.zoneName.value} already exists")
        }
        return PolicyResult.create(reasons)
    }
}