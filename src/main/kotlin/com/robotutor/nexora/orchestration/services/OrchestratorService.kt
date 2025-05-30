package com.robotutor.nexora.orchestration.services

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.device.models.DeviceType
import com.robotutor.nexora.feed.models.FeedType
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.*
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.orchestration.models.*
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.widget.models.WidgetType
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrchestratorService(
    private val userGateway: UserGateway,
    private val authGateway: AuthGateway,
    private val premisesGateway: PremisesGateway,
    private val deviceGateway: DeviceGateway,
    private val iamGateway: IAMGateway,
    private val kafkaPublisher: KafkaPublisher,
) {

    fun registerUser(request: UserRegistrationRequest): Mono<UserView> {
        return userGateway.registerUser(request.name, request.email)
            .flatMap { userView ->
                authGateway.registerUser(userView.userId, userView.email, request.password)
                    .map { userView }
            }
    }

    fun registerPremises(request: PremisesRegistrationRequest): Mono<PremisesView> {
        return premisesGateway.registerPremises(request.name)
            .flatMap { premises ->
                iamGateway.registerPremises(premises.premisesId)
                    .collectList()
                    .map { premises.addActors(it) }
            }
    }

    fun registerDevice(request: DeviceRegistrationRequest, invitationData: InvitationData): Mono<TokenView> {
        return createMono(getDeviceSeedData(invitationData))
            .flatMap { seed ->
                deviceGateway.registerDevice(request, seed.type, seed.feedCount)
                    .map {
                        seed.updateDeviceId(it.deviceId)
                        it
                    }
                    .flatMap { iamGateway.registerActorAsBot(it) }
                    .flatMap { premisesActorData ->
                        authGateway.createDeviceActorToken(premisesActorData)
                            .flatMap { kafkaPublisher.publish("feed.create", seed) { it } }
                            .contextWrite { it.put(PremisesActorData::class.java, premisesActorData) }
                    }
            }
    }


    fun getAllPremises(): Flux<PremisesView> {
        return iamGateway.getActors().collectList().flatMapMany { actors ->
            val premisesIds = actors.map { it.premisesId }.distinct()
            if (premisesIds.isEmpty()) Mono.empty()
            else {
                premisesGateway.getPremises(premisesIds).map { premises ->
                    premises.addActors(actors.filter { actor -> actor.premisesId == premises.premisesId })
                }
            }
        }
    }
}

fun getDeviceSeedData(invitationData: InvitationData): Device {
    return Device(
        modelNo = "GS4GTA25WF",
        feedCount = 12,
        feeds = listOf(
            FeedCreationRequest(
                Feed(name = "Light 1", type = FeedType.ACTUATOR, index = 0),
                Widget(name = "Light 1", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 1", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 2", type = FeedType.ACTUATOR, index = 1),
                Widget(name = "Light 2", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 2", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 3", type = FeedType.ACTUATOR, index = 2),
                Widget(name = "Light 3", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 3", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 4", type = FeedType.ACTUATOR, index = 3),
                Widget(name = "Light 4", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 4", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 5", type = FeedType.ACTUATOR, index = 4),
                Widget(name = "Light 5", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 5", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 6", type = FeedType.ACTUATOR, index = 5),
                Widget(name = "Light 6", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 6", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 7", type = FeedType.ACTUATOR, index = 6),
                Widget(name = "Light 7", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 7", access = AccessType.UPDATE))
            ),
            FeedCreationRequest(
                Feed(name = "Light 8", type = FeedType.ACTUATOR, index = 7),
                Widget(name = "Light 8", type = WidgetType.TOGGLE, zoneId = invitationData.zoneId),
                listOf(Policy(name = "Light 8", access = AccessType.UPDATE))
            ),
        ),
        type = DeviceType.DEVICE
    )
}