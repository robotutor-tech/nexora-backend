package com.robotutor.nexora.modules.orchestration.services

import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.feed.models.FeedType
import com.robotutor.nexora.modules.orchestration.models.Device
import com.robotutor.nexora.modules.orchestration.models.Feed
import com.robotutor.nexora.modules.orchestration.models.FeedCreationRequest
import com.robotutor.nexora.modules.orchestration.models.Widget
import com.robotutor.nexora.modules.widget.domain.model.WidgetType
import org.springframework.stereotype.Service

@Service
class OrchestratorService(
) {


//    fun registerDevice(request: DeviceRegistrationRequest, invitationData: InvitationData): Mono<TokenView> {
//        return createMono(getDeviceSeedData(invitationData))
//            .flatMap { seed ->
//                deviceGateway.registerDevice(request, seed.type, seed.feedCount)
//                    .map {
//                        seed.updateDeviceId(it.deviceId)
//                        it
//                    }
//                    .flatMap { device ->
//                        iamGateway.registerActorAsBot(device)
//                            .flatMap { premisesActorData ->
//                                authGateway.createDeviceActorToken(premisesActorData)
//                                    .flatMap {
//                                        val entitlementResource =
//                                            EntitlementResource(ResourceType.DEVICE, device.deviceId)
//                                        kafkaPublisher.publish("entitlement.create", entitlementResource) { it }
//                                    }
//                                    .flatMap { kafkaPublisher.publish("feeds.create", seed) { it } }
//                                    .contextWrite { it.put(PremisesActorData::class.java, premisesActorData) }
//                            }
//                    }
//            }
//    }
}

fun getDeviceSeedData(invitationData: InvitationData): Device {
    return Device(
        modelNo = "GS8GTAWF01",
        feedCount = 12,
        feeds = listOf(
            FeedCreationRequest(
                Feed(name = "Light 1", type = FeedType.ACTUATOR, index = 0),
                Widget(name = "Light 1", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 2", type = FeedType.ACTUATOR, index = 1),
                Widget(name = "Light 2", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 3", type = FeedType.ACTUATOR, index = 2),
                Widget(name = "Light 3", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 4", type = FeedType.ACTUATOR, index = 3),
                Widget(name = "Light 4", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 5", type = FeedType.ACTUATOR, index = 4),
                Widget(name = "Light 5", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 6", type = FeedType.ACTUATOR, index = 5),
                Widget(name = "Light 6", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 7", type = FeedType.ACTUATOR, index = 6),
                Widget(name = "Light 7", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
            FeedCreationRequest(
                Feed(name = "Light 8", type = FeedType.ACTUATOR, index = 7),
                Widget(name = "Light 8", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
            ),
        ),
        type = DeviceType.DEVICE
    )
}