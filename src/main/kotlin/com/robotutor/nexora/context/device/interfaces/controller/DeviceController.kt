package com.robotutor.nexora.context.device.interfaces.controller

import com.robotutor.nexora.context.device.application.usecase.DeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.ActivateDeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.RegisterDeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.UpdateMetaDataUseCase
import com.robotutor.nexora.context.device.interfaces.controller.view.ActivateDeviceRequest
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceResponse
import com.robotutor.nexora.context.device.interfaces.controller.view.RegisterDeviceRequest
import com.robotutor.nexora.context.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceMetaDataRequest
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.annotation.ResourceId
import com.robotutor.nexora.shared.application.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val activateDeviceUseCase: ActivateDeviceUseCase,
    private val deviceUseCase: DeviceUseCase,
    private val updateMetaDataUseCase: UpdateMetaDataUseCase,
) {
    @Authorize(ActionType.UPDATE, ResourceType.DEVICE)
    @PostMapping
    fun registerDevice(
        @RequestBody @Validated request: RegisterDeviceRequest,
        actorData: ActorData
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toRegisterDeviceCommand(request, actorData)
        return registerDeviceUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.DEVICE)
    @GetMapping
    fun getDevices(actorData: ActorData, resources: AuthorizedResources): Flux<DeviceResponse> {
        val query = DeviceMapper.toGetDevicesQuery(resources, actorData)
        return deviceUseCase.execute(query)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, ResourceSelector.SPECIFIC)
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

    @PatchMapping("/metadata")
    fun validateMetaData(@RequestBody metadata: DeviceMetaDataRequest, actorData: ActorData): Mono<DeviceResponse> {
        val command = DeviceMapper.toUpdateMetaDataCommand(metadata, actorData)
        return updateMetaDataUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, ResourceSelector.SPECIFIC)
//    @PatchMapping("/{deviceId}/commission")
//    fun validateMetaData(@PathVariable @ResourceId deviceId: String, actorData: ActorData): Mono<DeviceResponse> {
//        val command = DeviceMapper.toUpdateMetaDataCommand(metadata, accountData)
//        return updateMetaDataUseCase.execute(command)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }


//    @GetMapping("/{deviceId}")
//    fun getDevice(@PathVariable deviceId: String): Mono<DeviceResponse> {
//        return deviceUseCase.getDevice(DeviceId(deviceId))
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

    @GetMapping("/me")
    fun getDevice(accountData: AccountData): Mono<DeviceResponse> {
        return deviceUseCase.execute(accountData.accountId)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @PatchMapping("/health")
//    fun getDevice(@RequestBody @Validated healthRequest: HealthRequest, deviceData: DeviceData): Mono<DeviceResponse> {
//        val health = DeviceMapper.toDeviceHealth(healthRequest)
//        return deviceUseCase.updateDeviceHealth(health, deviceData)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }
}