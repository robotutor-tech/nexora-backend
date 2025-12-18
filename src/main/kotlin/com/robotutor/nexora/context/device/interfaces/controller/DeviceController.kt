package com.robotutor.nexora.context.device.interfaces.controller

import com.robotutor.nexora.context.device.application.usecase.ActivateDeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.RegisterDeviceUseCase
import com.robotutor.nexora.context.device.interfaces.controller.dto.ActivateDeviceRequest
import com.robotutor.nexora.context.device.interfaces.controller.dto.DeviceResponse
import com.robotutor.nexora.context.device.interfaces.controller.dto.RegisterDeviceRequest
import com.robotutor.nexora.context.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.annotation.ResourceId
import com.robotutor.nexora.shared.application.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val activateDeviceUseCase: ActivateDeviceUseCase,
) {
    @Authorize(ActionType.WRITE, ResourceType.DEVICE)
    @PostMapping
    fun registerDevice(
        @RequestBody @Validated request: RegisterDeviceRequest,
        actorData: ActorData,
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toRegisterDeviceCommand(request, actorData)
        return registerDeviceUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @Authorize(ActionType.WRITE, ResourceType.DEVICE, ResourceSelector.SPECIFIC)
    @PostMapping("/{deviceId}/activate")
    fun activateDevice(
        @PathVariable @ResourceId deviceId: String,
        @RequestBody @Validated request: ActivateDeviceRequest,
        actorData: ActorData,
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toActivateDeviceCommand(deviceId, request, actorData)
        return activateDeviceUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @Authorize(ActionType.READ, ResourceType.DEVICE)
//    @GetMapping
//    fun getDevices(actorData: ActorData, resourcesData: ResourcesData): Flux<DeviceResponse> {
//        val deviceIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.DEVICE).map { DeviceId(it) }
//        return deviceUseCase.getDevices(actorData.premisesId, deviceIds)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

//    @GetMapping("/{deviceId}")
//    fun getDevice(@PathVariable deviceId: String): Mono<DeviceResponse> {
//        return deviceUseCase.getDevice(DeviceId(deviceId))
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

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