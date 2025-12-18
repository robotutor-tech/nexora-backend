package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.orchestration.client.DeviceClient
import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationResponse
import com.robotutor.nexora.orchestration.messaging.event.CompensateDeviceRegistrationEvent
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
        return deviceClient.registerDevice(device)
            .flatMap { deviceResponse ->
                val secret = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "")
                iamClient.registerAccount(deviceResponse, secret)
                    .map { DeviceRegistrationResponse.from(deviceResponse, secret) }
                    .onErrorResume { throwable ->
                        eventPublisher.publish(CompensateDeviceRegistrationEvent(deviceResponse.deviceId), throwable)
                    }
            }
    }
}
