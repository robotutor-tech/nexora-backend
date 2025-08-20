package com.robotutor.nexora.modules.orchestration.services

import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.feed.models.FeedType
import com.robotutor.nexora.modules.orchestration.gateway.AuthGateway
import com.robotutor.nexora.modules.orchestration.gateway.DeviceGateway
import com.robotutor.nexora.modules.orchestration.gateway.IAMGateway
import com.robotutor.nexora.modules.orchestration.gateway.PremisesGateway
import com.robotutor.nexora.modules.orchestration.gateway.view.PremisesWithActorView
import com.robotutor.nexora.modules.orchestration.models.Device
import com.robotutor.nexora.modules.orchestration.models.Feed
import com.robotutor.nexora.modules.orchestration.models.FeedCreationRequest
import com.robotutor.nexora.modules.orchestration.models.Widget
import com.robotutor.nexora.modules.widget.models.WidgetType
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrchestratorService(
    private val authGateway: AuthGateway,
    private val premisesGateway: PremisesGateway,
    private val deviceGateway: DeviceGateway,
    private val iamGateway: IAMGateway,
    private val kafkaPublisher: KafkaPublisher,
) {

//    fun registerUser(request: UserRegistrationRequest): Mono<UserView> {
//        return sagaService.startSaga("USER_REGISTRATION", mapOf("name" to request.name, "email" to request.email))
//            .flatMap { saga ->
//                saga.addStep("RegisterUser", mapOf("email" to request.email, "name" to request.name))
//                userGateway.registerUser(request.name, request.email)
//                    .addCompensate(saga, "CompensateUser", { mapOf("userId" to it.userId) }) {
//                        kafkaPublisher.publish(
//                            "saga.compensate.user.delete",
//                            CompensateCommand(saga.saga.sagaId, it.userId)
//                        )
//                    }
//                    .flatMap { userView ->
//                        saga.completeStep("RegisterUser")
//                            .addStep("RegisterAuthUser", mapOf("userId" to userView.userId))
//                        authGateway.registerUser(userView.userId, userView.email, request.password)
//                            .map {
//                                saga.completeStep("RegisterAuthUser")
//                                userView
//                            }
//                    }
//                    .compensate(sagaService, saga, NexoraError.NEXORA0301)
//            }
//    }
//
//    fun registerPremises(request: PremisesRegistrationRequest): Mono<PremisesWithActorView> {
//        return sagaService.startSaga("PREMISES_REGISTRATION", request.toMap())
//            .flatMap { saga ->
//                saga.addStep("RegisterPremises", request.toMap())
//                premisesGateway.registerPremises(request.name)
//                    .addCompensate(saga, "CompensatePremises", { mapOf("premisesId" to it.premisesId) }) {
//                        kafkaPublisher.publish(
//                            "saga.compensate.premises.delete",
//                            CompensateCommand(saga.saga.sagaId, it.premisesId)
//                        )
//                    }
//                    .flatMap { premises ->
//                        saga.completeStep("RegisterPremises")
//                            .addStep("RegisterIAM", mapOf("premisesId" to premises.premisesId))
//                        iamGateway.registerPremises(premises.premisesId)
//                            .map {
//                                saga.completeStep("RegisterIAM")
//                                PremisesWithActorView.from(premises, it)
//                            }
//                    }
//                    .compensate(sagaService, saga, NexoraError.NEXORA0302)
//            }
//    }

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

    fun getAllPremises(): Flux<PremisesWithActorView> {
        return iamGateway.getActors().collectList().flatMapMany { actors ->
            val premisesIds = actors.map { it.premisesId }.distinct()
            if (premisesIds.isEmpty()) Mono.empty()
            else {
                premisesGateway.getPremises(premisesIds)
                    .map { premises ->
                        PremisesWithActorView.from(premises, actors.find { it.premisesId == premises.premisesId }!!)
                    }
            }
        }
    }
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