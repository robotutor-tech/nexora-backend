package com.robotutor.nexora.module.zone.domain.policy

import com.robotutor.nexora.module.zone.application.command.CreateWidgetsCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class CreateWidgetsPolicy : Policy<CreateWidgetsCommand> {
    override fun evaluate(input: CreateWidgetsCommand): PolicyResult {
        /**  Add validation logics:
         * Zone present and model no is valid
         * validate the number of feeds and no of widgets in model no is equal
         * feeds are not already assigned to other widgets
         */
        return PolicyResult.allow()
    }
}