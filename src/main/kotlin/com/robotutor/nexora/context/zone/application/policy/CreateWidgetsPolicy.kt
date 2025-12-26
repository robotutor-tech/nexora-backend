package com.robotutor.nexora.context.zone.application.policy

import com.robotutor.nexora.context.zone.application.command.CreateWidgetsCommand
import com.robotutor.nexora.context.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.context.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateWidgetsPolicy(private val zoneRepository: ZoneRepository) : Policy<CreateWidgetsCommand> {
    override fun evaluate(command: CreateWidgetsCommand): Mono<PolicyResult> {
        /**  Add validation logics:
         * Zone present and model no is valid
         * validate the number of feeds and no of widgets in model no is equal
         * feeds are not already assigned to other widgets
         */
        return createMono(PolicyResult.allow())
    }
}