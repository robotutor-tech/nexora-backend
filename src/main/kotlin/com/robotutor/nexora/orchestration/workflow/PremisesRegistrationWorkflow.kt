package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.client.PremisesClient
import com.robotutor.nexora.orchestration.client.view.PremisesResponse
import com.robotutor.nexora.orchestration.controller.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.messaging.event.CompensatePremisesRegistrationEvent
import com.robotutor.nexora.orchestration.messaging.event.OrchestrationEvent
import com.robotutor.nexora.shared.domain.event.EventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PremisesRegistrationWorkflow(
    private val iamClient: IAMClient,
    private val premisesClient: PremisesClient,
    private val eventPublisher: EventPublisher<OrchestrationEvent>,
) {
    fun registerPremises(request: PremisesRegistrationRequest): Mono<PremisesResponse> {
        return premisesClient.registerPremises(request)
            .flatMap { premises ->
                iamClient.registerActorForOwner(premises.premisesId)
                    .map { premises }
                    .onErrorResume { throwable ->
                        eventPublisher.publish(CompensatePremisesRegistrationEvent(premises.premisesId), throwable)
                    }
            }
    }
}
