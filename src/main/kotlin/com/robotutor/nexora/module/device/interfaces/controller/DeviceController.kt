package com.robotutor.nexora.module.device.interfaces.controller

import com.robotutor.nexora.common.resource.annotation.ResourceSelector
import com.robotutor.nexora.module.device.application.command.GetDeviceQuery
import com.robotutor.nexora.module.device.application.service.CommissionDeviceService
import com.robotutor.nexora.module.device.application.service.GetDeviceService
import com.robotutor.nexora.module.device.application.service.RegisterDeviceService
import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.device.interfaces.controller.mapper.DeviceMapper
import com.robotutor.nexora.module.device.interfaces.controller.view.DeviceMetaDataRequest
import com.robotutor.nexora.module.device.interfaces.controller.view.DeviceResponse
import com.robotutor.nexora.module.device.interfaces.controller.view.RegisterDeviceRequest
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val registerDeviceService: RegisterDeviceService,
    private val commissionDeviceService: CommissionDeviceService,
    private val getDeviceService: GetDeviceService,
) {
    @PostMapping
    fun registerDevice(
        @RequestBody @Validated request: RegisterDeviceRequest,
        actorData: ActorData
    ): Mono<DeviceResponse> {
        val command = DeviceMapper.toRegisterDeviceCommand(request, actorData)
        return registerDeviceService.execute(command)
            .map { DeviceMapper.toDeviceResponse(it) }
    }

    @GetMapping
    fun getDevices(
        actorData: ActorData,
        @ResourceSelector(ActionType.READ, ResourceType.DEVICE) resources: Resources
    ): Flux<DeviceResponse> {
        val query = DeviceMapper.toGetDevicesQuery(resources, actorData)
        return getDeviceService.execute(query)
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
//        return deviceService.getDevice(DeviceId(deviceId))
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }

    @GetMapping("/me")
    fun getDevice(accountData: AccountData): Mono<DeviceResponse> {
        return getDeviceService.execute(GetDeviceQuery(DeviceId(accountData.principalId.value)))
            .map { DeviceMapper.toDeviceResponse(it) }
    }

//    @PatchMapping("/health")
//    fun getDevice(@RequestBody @Validated healthRequest: HealthRequest, deviceData: DeviceData): Mono<DeviceResponse> {
//        val health = DeviceMapper.toDeviceHealth(healthRequest)
//        return deviceService.updateDeviceHealth(health, deviceData)
//            .map { DeviceMapper.toDeviceResponse(it) }
//    }
}