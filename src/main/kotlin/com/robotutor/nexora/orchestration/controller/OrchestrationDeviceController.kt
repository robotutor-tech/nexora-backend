package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.orchestration.client.view.ActorResponse
import com.robotutor.nexora.orchestration.controller.view.RegisterDeviceActorRequest
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationResponse
import com.robotutor.nexora.orchestration.workflow.RegisterDeviceActorWorkflow
import com.robotutor.nexora.orchestration.workflow.DeviceRegistrationWorkflow
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestration/devices")
class OrchestrationDeviceController(
    private val deviceRegistrationWorkflow: DeviceRegistrationWorkflow,
    private val registerDeviceActor: RegisterDeviceActorWorkflow,
) {
    @Authorize(ActionType.UPDATE, ResourceType.DEVICE)
    @PostMapping("/register")
    fun registerDevice(@RequestBody @Validated device: DeviceRegistrationRequest): Mono<DeviceRegistrationResponse> {
        return deviceRegistrationWorkflow.registerDevice(device)
    }

    @PostMapping("/actors/register")
    fun registerDeviceActor(@RequestBody @Validated device: RegisterDeviceActorRequest): Mono<ActorResponse> {
        return registerDeviceActor.register(device)
    }
}