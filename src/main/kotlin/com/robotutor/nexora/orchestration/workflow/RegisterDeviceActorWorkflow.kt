package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.orchestration.client.DeviceClient
import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.client.view.ActorResponse
import com.robotutor.nexora.orchestration.controller.view.RegisterDeviceActorRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDeviceActorWorkflow(
    private val deviceClient: DeviceClient,
    private val iamClient: IAMClient,
) {
    fun register(device: RegisterDeviceActorRequest): Mono<ActorResponse> {
        return deviceClient.updateDeviceMetaData(device)
            .flatMap { iamClient.registerActorForDevice(it) }
    }
}
