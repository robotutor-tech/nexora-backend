package com.robotutor.nexora.device.controllers

import com.robotutor.nexora.device.controllers.view.DeviceRequest
import com.robotutor.nexora.device.controllers.view.DeviceView
import com.robotutor.nexora.device.services.DeviceService
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(private val deviceService: DeviceService) {

    @PostMapping("/register")
    fun registerDevice(
        @RequestBody @Validated deviceRequest: DeviceRequest,
        invitationData: InvitationData
    ): Mono<DeviceView> {
        return deviceService.register(deviceRequest, invitationData).map { DeviceView.from(it) }
    }

    @GetMapping
    fun getDevices(premisesActorData: PremisesActorData): Flux<DeviceView> {
        return deviceService.getDevices(premisesActorData).map { DeviceView.from(it) }
    }
}
