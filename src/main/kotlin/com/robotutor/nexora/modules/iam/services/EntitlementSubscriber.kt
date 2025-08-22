package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.shared.domain.model.ResourceType
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class EntitlementSubscriber(
) {

    @PostConstruct
    fun init() {
//        kafkaConsumer.consume(listOf("entitlement.create"), EntitlementResource::class.java) {
//            Mono.deferContextual { ctx ->
//                val premisesActorData = ctx.get(PremisesActorData::class.java)
//                entitlementService.createAndAssignNewResource(it.message, premisesActorData)
//                    .collectList()
//            }
//        }
//            .subscribe()
    }
}


data class EntitlementResource(
    val resourceType: ResourceType,
    val resourceId: String,
)
