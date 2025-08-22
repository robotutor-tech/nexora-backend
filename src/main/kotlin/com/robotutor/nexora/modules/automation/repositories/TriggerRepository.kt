//package com.robotutor.nexora.modules.automation.repositories
//
//import com.robotutor.nexora.modules.automation.models.TriggerId
//import com.robotutor.nexora.modules.automation.models.documents.TriggerDocument
//import com.robotutor.nexora.common.security.models.PremisesId
//import org.springframework.data.mongodb.repository.Query
//import org.springframework.data.repository.reactive.ReactiveCrudRepository
//import org.springframework.stereotype.Repository
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//
//@Repository
//interface TriggerRepository : ReactiveCrudRepository<TriggerDocument, TriggerId> {
//    fun findAllByTriggerIdInAndPremisesId(triggers: List<TriggerId>, premisesId: PremisesId): Flux<TriggerDocument>
//    fun findByTriggerIdAndPremisesId(triggerId: TriggerId, premisesId: PremisesId): Mono<TriggerDocument>
//
//    @Query("""{premisesId: ?0, "config.commands": {""" + "\$in: [{\$regex: ?1, \$options: \"i\"}]" + """ }}""")
//    fun findAllByPremisesIdAndVoiceCommands(premisesId: PremisesId, commands: String): Flux<TriggerDocument>
//    fun findByPremisesIdAndConfig(premisesId: PremisesId, config: Map<String, Any?>): Mono<TriggerDocument>
//}
