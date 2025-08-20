package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.shared.adapters.messaging.services.KafkaConsumer
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EntitlementSubscriber(
    private val kafkaConsumer: KafkaConsumer,
    private val entitlementService: EntitlementService
) {

    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("entitlement.create"), EntitlementResource::class.java) {
            Mono.deferContextual { ctx ->
                val premisesActorData = ctx.get(PremisesActorData::class.java)
                entitlementService.createAndAssignNewResource(it.message, premisesActorData)
                    .collectList()
            }
        }
            .subscribe()
    }
}


data class EntitlementResource(
    val resourceType: ResourceType,
    val resourceId: String,
)
