package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.Trigger
import com.robotutor.nexora.automation.models.TriggerId
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TriggerRepository : ReactiveCrudRepository<Trigger, TriggerId> {
    fun findAllByTriggerIdInAndPremisesId(triggers: List<TriggerId>, premisesId: PremisesId): Flux<Trigger>
    @Query("""{"premisesId": ?0, "type": "VOICE", "config.commands": { "$in": ?1}}""")
    fun findByPremisesIdAndVoiceCommand(premisesId: PremisesId, commands: List<String>): Flux<Trigger>
}
