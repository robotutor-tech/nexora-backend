package com.robotutor.nexora.modules.device.interfaces.controller

import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceRequest
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceResponse
import com.robotutor.nexora.modules.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.modules.device.application.DeviceUseCase
import com.robotutor.nexora.common.security.filters.annotations.ActionType
import com.robotutor.nexora.common.security.filters.annotations.RequireAccess
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.ResourcesData
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController("/devices")
class DeviceController(
    private val deviceUseCase: DeviceUseCase,
) {
    @PostMapping("/register")
    fun registerDevice(
        @RequestBody @Validated deviceRequest: DeviceRequest,
        invitationData: InvitationData
    ): Mono<DeviceResponse> {
        val createDeviceCommand = DeviceMapper.toCreateDeviceCommand(deviceRequest, invitationData)
        return deviceUseCase.register(createDeviceCommand)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.DEVICE)
    @GetMapping
    fun getDevices(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<DeviceResponse> {
        val deviceIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.DEVICE).map { DeviceId(it) }
        return deviceUseCase.getDevices(PremisesId(premisesActorData.premisesId), deviceIds)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.DEVICE, "deviceId")
    @GetMapping("/{deviceId}")
    fun getDevice(@PathVariable deviceId: String, premisesActorData: PremisesActorData): Mono<DeviceResponse> {
        return deviceUseCase.getDevice(DeviceId(deviceId), PremisesId(premisesActorData.premisesId))
            .map { DeviceMapper.toDeviceResponse(it) }
    }
}