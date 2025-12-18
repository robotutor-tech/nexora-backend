package com.robotutor.nexora.context.zone.application.policy

import com.robotutor.nexora.context.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.context.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateZonePolicy(private val zoneRepository: ZoneRepository) : Policy<CreateZoneCommand> {
    override fun evaluate(command: CreateZoneCommand): Mono<PolicyResult> {
        return zoneRepository.findByPremisesIdAndName(command.premisesId, command.name)
            .map { PolicyResult.deny(listOf("Zone with name ${command.name} already exists")) }
            .switchIfEmpty(createMono(PolicyResult.allow()))
    }
}