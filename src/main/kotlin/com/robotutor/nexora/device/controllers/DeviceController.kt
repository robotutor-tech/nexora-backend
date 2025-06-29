package com.robotutor.nexora.device.controllers

import com.robotutor.nexora.device.controllers.view.DeviceRequest
import com.robotutor.nexora.device.controllers.view.DeviceView
import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.device.services.DeviceService
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.RequireAccess
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @RequireAccess(ActionType.LIST, ResourceType.DEVICE)
    @GetMapping
    fun getDevices(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<DeviceView> {
        val deviceIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.DEVICE)
        return deviceService.getDevices(premisesActorData, deviceIds).map { DeviceView.from(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.DEVICE, "deviceId")
    @GetMapping("/{deviceId}")
    fun getDevice(@PathVariable deviceId: DeviceId, premisesActorData: PremisesActorData): Mono<DeviceView> {
        return deviceService.getDevice(deviceId, premisesActorData).map { DeviceView.from(it) }
    }
}
