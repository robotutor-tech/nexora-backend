package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationResponse
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
@RequestMapping("/orchestration")
class DeviceRegistrationController(
    private val deviceRegistrationWorkflow: DeviceRegistrationWorkflow,
) {
    @Authorize(ActionType.WRITE, ResourceType.DEVICE)
    @PostMapping("/devices/register")
    fun registerUser(@RequestBody @Validated device: DeviceRegistrationRequest): Mono<DeviceRegistrationResponse> {
        return deviceRegistrationWorkflow.registerDevice(device)
    }
}