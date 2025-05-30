package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.PolicyRequest
import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.PremisesActorData
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PoliciesCreateSubscriber(
    private val policyService: PolicyService,
    private val kafkaConsumer: KafkaConsumer,
    private val roleService: RoleService,
) {

    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("policies.create"), PoliciesRequest::class.java) {
            Mono.deferContextual { ctx ->
                val premisesActorData = ctx.get(PremisesActorData::class.java)
                createFlux(it.message.policies)
                    .flatMap { policyRequest -> policyService.createPolicy(policyRequest, premisesActorData) }
                    .collectList()
                    .flatMap { policies ->
                        roleService.assignPoliciesToCurrentActor(policies, premisesActorData)
                            .flatMapMany { roleService.assignPolicyToHumanRole(policies, premisesActorData) }
                            .collectList()
                    }
            }
        }
            .subscribe()
    }
}

data class PoliciesRequest(
    val policies: List<PolicyRequest>,
)