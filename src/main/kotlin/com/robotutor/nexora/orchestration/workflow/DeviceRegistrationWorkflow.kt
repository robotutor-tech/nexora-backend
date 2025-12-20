package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.orchestration.client.DeviceClient
import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.client.view.AccountPayload
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationResponse
import com.robotutor.nexora.orchestration.messaging.event.CompensateAccountRegistrationEvent
import com.robotutor.nexora.orchestration.messaging.event.OrchestrationEvent
import com.robotutor.nexora.shared.domain.event.EventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class DeviceRegistrationWorkflow(
    private val deviceClient: DeviceClient,
    private val iamClient: IAMClient,
    private val eventPublisher: EventPublisher<OrchestrationEvent>,
) {
    fun registerDevice(device: DeviceRegistrationRequest): Mono<DeviceRegistrationResponse> {
        val payload = AccountPayload(
            credentialId = UUID.randomUUID().toString(),
            secret = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", ""),
            kind = "API_SECRET",
            type = "MACHINE"
        )
        return iamClient.registerAccount(payload)
            .flatMap { iamAccount ->
                deviceClient.registerDevice(device, iamAccount)
                    .map {
                        DeviceRegistrationResponse.from(
                            it,
//                    payload.credentialId,
                            payload.secret
                        )
                    }
                    .onErrorResume { throwable ->
                        eventPublisher.publish(CompensateAccountRegistrationEvent(iamAccount.accountId), throwable)
                    }
            }
    }
}
