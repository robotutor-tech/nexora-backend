package com.robotutor.nexora.modules.iam.services

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


