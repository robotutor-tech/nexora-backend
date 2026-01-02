package com.robotutor.nexora.context.device.interfaces.controller

import com.robotutor.nexora.common.security.domain.vo.AuthorizedResources
import com.robotutor.nexora.context.device.application.command.GetDeviceQuery
import com.robotutor.nexora.context.device.application.service.CommissionDeviceService
import com.robotutor.nexora.context.device.application.service.DeviceUseCase
import com.robotutor.nexora.context.device.application.service.RegisterDeviceUseCase
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceMetaDataRequest
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceResponse
import com.robotutor.nexora.context.device.interfaces.controller.view.RegisterDeviceRequest
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val commissionDeviceService: CommissionDeviceService,
    private val deviceUseCase: DeviceUseCase,
) {
    @PostMapping
    fun registerDevice(
        @RequestBody @Validated request: RegisterDeviceRequest,
        actorData: ActorData
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toRegisterDeviceCommand(request, actorData)
        return registerDeviceUseCase.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @GetMapping
    fun getDevices(actorData: ActorData, resources: AuthorizedResources): Flux<DeviceResponse> {
        val query = DeviceMapper.toGetDevicesQuery(resources, actorData)
        return deviceUseCase.execute(query)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @PostMapping("/commission")
    fun commissionDevice(
        @RequestBody @Validated metadata: DeviceMetaDataRequest,
        actorData: ActorData
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toCommissionDeviceCommand(metadata, actorData)
        return commissionDeviceService.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }


//    @GetMapping("/{deviceId}")
//    fun getDevice(@PathVariable deviceId: String): Mono<DeviceResponse> {
//        return deviceUseCase.getDevice(DeviceId(deviceId))
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

    @GetMapping("/me")
    fun getDevice(accountData: AccountData): Mono<DeviceResponse> {
        return deviceUseCase.execute(GetDeviceQuery(DeviceId(accountData.principalId.value)))
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @PatchMapping("/health")
//    fun getDevice(@RequestBody @Validated healthRequest: HealthRequest, deviceData: DeviceData): Mono<DeviceResponse> {
//        val health = DeviceMapper.toDeviceHealth(healthRequest)
//        return deviceUseCase.updateDeviceHealth(health, deviceData)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }
}