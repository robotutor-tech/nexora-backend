package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controller.view.DeviceRegistrationResponse
import com.robotutor.nexora.orchestration.workflow.DeviceRegistrationWorkflow
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
    @PostMapping("/devices/register")
    fun registerUser(@RequestBody @Validated device: DeviceRegistrationRequest): Mono<DeviceRegistrationResponse> {
        return deviceRegistrationWorkflow.registerDevice(device)
    }
}