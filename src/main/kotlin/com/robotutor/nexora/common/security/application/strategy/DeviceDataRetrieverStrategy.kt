package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.DeviceDataRetriever
import com.robotutor.nexora.common.security.application.ports.UserDataRetriever
import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.DeviceData
import com.robotutor.nexora.shared.domain.model.UserContext
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DeviceDataRetrieverStrategy(
    private val deviceDataRetriever: DeviceDataRetriever
) : DataRetrieverStrategy<DeviceContext, DeviceData> {
    override fun getPrincipalData(context: DeviceContext): Mono<DeviceData> {
        return deviceDataRetriever.getDeviceData(context.deviceId)
    }
}