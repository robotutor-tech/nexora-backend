package com.robotutor.nexora.context.device.interfaces.controller

import com.robotutor.nexora.context.device.application.usecase.CommissionDeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.DeviceUseCase
import com.robotutor.nexora.context.device.application.usecase.RegisterDeviceUseCase
import com.robotutor.nexora.context.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceMetaDataRequest
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceResponse
import com.robotutor.nexora.context.device.interfaces.controller.view.RegisterDeviceRequest
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val commissionDeviceUseCase: CommissionDeviceUseCase,
    private val deviceUseCase: DeviceUseCase,
) {
    @Authorize(ActionType.UPDATE, ResourceType.DEVICE)
    @PostMapping
    fun registerDevice(
        @RequestBody @Validated request: RegisterDeviceRequest,
        ActorData: ActorData
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toRegisterDeviceCommand(request, ActorData)
        return registerDeviceUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.DEVICE)
    @GetMapping
    fun getDevices(ActorData: ActorData, resources: AuthorizedResources): Flux<DeviceResponse> {
        val query = DeviceMapper.toGetDevicesQuery(resources, ActorData)
        return deviceUseCase.execute(query)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.DEVICE, "#Actor.accountId.value")
    @PostMapping("/commission")
    fun commissionDevice(
        @RequestBody @Validated metadata: DeviceMetaDataRequest,
        ActorData: ActorData
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toCommissionDeviceCommand(metadata, ActorData)
        return commissionDeviceUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }


//    @GetMapping("/{deviceId}")
//    fun getDevice(@PathVariable deviceId: String): Mono<DeviceResponse> {
//        return deviceUseCase.getDevice(DeviceId(deviceId))
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

    @GetMapping("/me")
    fun getDevice(AccountData: AccountData): Mono<DeviceResponse> {
        return deviceUseCase.execute(AccountData.accountId)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @PatchMapping("/health")
//    fun getDevice(@RequestBody @Validated healthRequest: HealthRequest, deviceData: DeviceData): Mono<DeviceResponse> {
//        val health = DeviceMapper.toDeviceHealth(healthRequest)
//        return deviceUseCase.updateDeviceHealth(health, deviceData)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }
}