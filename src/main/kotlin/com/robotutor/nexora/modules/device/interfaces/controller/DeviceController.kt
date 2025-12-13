package com.robotutor.nexora.modules.device.interfaces.controller

import com.robotutor.nexora.modules.device.application.DeviceUseCase
import com.robotutor.nexora.modules.device.application.RegisterDeviceUseCase
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceResponse
import com.robotutor.nexora.modules.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val deviceUseCase: DeviceUseCase,
    private val registerDeviceUseCase: RegisterDeviceUseCase,
) {
//    @PostMapping
//    fun registerDevice(
//        @RequestBody @Validated deviceRequest: DeviceRequest,
//        invitationData: InvitationData
//    ): Mono<Device> {
//        val createDeviceCommand = DeviceMapper.toCreateDeviceCommand(deviceRequest)
//        return registerDeviceUseCase.register(createDeviceCommand, invitationData)
//    }

    @RequireAccess(ActionType.READ, ResourceType.DEVICE)
    @GetMapping
    fun getDevices(actorData: ActorData, resourcesData: ResourcesData): Flux<DeviceResponse> {
        val deviceIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.DEVICE).map { DeviceId(it) }
        return deviceUseCase.getDevices(actorData.premisesId, deviceIds)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @GetMapping("/{deviceId}")
    fun getDevice(@PathVariable deviceId: String): Mono<DeviceResponse> {
        return deviceUseCase.getDevice(DeviceId(deviceId))
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @GetMapping("/me")
//    fun getDevice(deviceData: DeviceData): Mono<DeviceResponse> {
//        return deviceUseCase.getDevice(deviceData.deviceId)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

//    @PatchMapping("/health")
//    fun getDevice(@RequestBody @Validated healthRequest: HealthRequest, deviceData: DeviceData): Mono<DeviceResponse> {
//        val health = DeviceMapper.toDeviceHealth(healthRequest)
//        return deviceUseCase.updateDeviceHealth(health, deviceData)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }
}