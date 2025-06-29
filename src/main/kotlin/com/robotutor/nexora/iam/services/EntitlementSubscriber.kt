package com.robotutor.nexora.iam.services

import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
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
