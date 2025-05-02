package com.robotutor.nexora.orchestration.services

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.device.models.DeviceType
import com.robotutor.nexora.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.AuthGateway
import com.robotutor.nexora.orchestration.gateway.DeviceGateway
import com.robotutor.nexora.orchestration.gateway.FeedGateway
import com.robotutor.nexora.orchestration.gateway.IAMGateway
import com.robotutor.nexora.orchestration.gateway.PremisesGateway
import com.robotutor.nexora.orchestration.gateway.UserGateway
import com.robotutor.nexora.orchestration.gateway.WidgetGateway
import com.robotutor.nexora.orchestration.gateway.view.FeedView
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.orchestration.models.Device
import com.robotutor.nexora.orchestration.models.Feed
import com.robotutor.nexora.orchestration.models.FeedType
import com.robotutor.nexora.orchestration.models.Widget
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.widget.controllers.view.WidgetView
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
    private val feedGateway: FeedGateway,
    private val iamGateway: IAMGateway,
    private val widgetGateway: WidgetGateway,
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
        return createMono(getDeviceSeedData())
            .flatMap { seed ->
                deviceGateway.registerDevice(request, seed.type)
                    .flatMap { iamGateway.registerActorAsBot(it) }
                    .flatMap { authGateway.createDeviceActorToken(it) }
                    .flatMap { tokenView ->
                        createFeeds(seed.feeds, tokenView)
                            .collectList()
                            .flatMapMany { createWidgets(seed.widgets, it, invitationData, tokenView) }
                            .collectList()
                            .map { tokenView }
                    }
            }
    }

    private fun createWidgets(
        widgets: List<Widget>,
        feeds: List<FeedView>,
        invitationData: InvitationData,
        tokenView: TokenView
    ): Flux<WidgetView> {
        return createFlux(widgets)
            .flatMapSequential { widget ->
                val feed = feeds.find { it.name == widget.name }!!
                widgetGateway.createWidget(widget, feed, invitationData.zoneId, tokenView)
            }
    }

    private fun createFeeds(feeds: List<Feed>, tokenView: TokenView): Flux<FeedView> {
        return createFlux(feeds)
            .flatMapSequential { feed -> feedGateway.createFeed(feed, tokenView) }
    }

    fun getAllPremises(): Flux<PremisesView> {
        return iamGateway.getActors()
            .collectList()
            .flatMapMany { actors ->
                val premisesIds = actors.map { it.premisesId }.distinct()
                if (premisesIds.isEmpty()) Mono.empty()
                else {
                    premisesGateway.getPremises(premisesIds)
                        .map { premises ->
                            premises.addActors(actors.filter { actor -> actor.premisesId == premises.premisesId })
                        }
                }
            }
    }
}

fun getDeviceSeedData(): Device {
    return Device(
        modelNo = "GS4GTA25WF",
        feeds = listOf(
            Feed(name = "Light 1", type = FeedType.ACTUATOR),
            Feed(name = "Light 2", type = FeedType.ACTUATOR),
            Feed(name = "Light 3", type = FeedType.ACTUATOR),
            Feed(name = "Light 4", type = FeedType.ACTUATOR),
        ),
        widgets = listOf(
            Widget(name = "Light 1", type = WidgetType.TOGGLE),
            Widget(name = "Light 2", type = WidgetType.TOGGLE),
            Widget(name = "Light 3", type = WidgetType.TOGGLE),
            Widget(name = "Light 4", type = WidgetType.TOGGLE),
        ),
        rules = emptyList(),
        type = DeviceType.DEVICE
    )
}