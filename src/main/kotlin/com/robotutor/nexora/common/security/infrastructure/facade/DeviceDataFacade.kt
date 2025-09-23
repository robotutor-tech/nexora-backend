package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.DeviceDataRetriever
import com.robotutor.nexora.modules.device.interfaces.controller.DeviceController
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.DeviceData
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityDeviceDataClient")
class DeviceDataFacade(private val deviceController: DeviceController) : DeviceDataRetriever {
    override fun getDeviceData(deviceId: DeviceId): Mono<DeviceData> {
        return ContextDataResolver.getActorData()
            .flatMap { actorData -> deviceController.getDevice(deviceId.value, actorData) }
            .map { response ->
                DeviceData(
                    deviceId = DeviceId(response.deviceId),
                    premisesId = PremisesId(response.premisesId),
                    name = Name(response.name),
                )
            }
    }
}