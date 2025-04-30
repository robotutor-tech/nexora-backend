package com.robotutor.nexora.orchestration.services

import com.robotutor.nexora.orchestration.controllers.view.DeviceRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.AuthGateway
import com.robotutor.nexora.orchestration.gateway.DeviceGateway
import com.robotutor.nexora.orchestration.gateway.FeedGateway
import com.robotutor.nexora.orchestration.gateway.IAMGateway
import com.robotutor.nexora.orchestration.gateway.PremisesGateway
import com.robotutor.nexora.orchestration.gateway.UserGateway
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.orchestration.models.Board
import com.robotutor.nexora.orchestration.models.Feed
import com.robotutor.nexora.security.createFlux
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

    fun registerDevice(request: DeviceRegistrationRequest): Flux<Feed> {
        return authGateway.validateInvitation(request.modelNo)
            .flatMap { invitationView ->
                deviceGateway.registerDevice(invitationView, request)
            }
            .flatMapMany {
                val board = createBoard()
                createFlux(board.feeds)
                    .flatMap { feed ->
                        feedGateway.createFeed(feed)
                            .map {
                                feed.updateFeedView(it)
                            }
                    }
            }
    }

    fun getAllPremises(): Flux<PremisesView> {
        return iamGateway.getActors()
            .collectList()
            .flatMapMany { actors ->
                val premisesIds = actors.map { it.premisesId }.distinct()
                premisesGateway.getPremises(premisesIds)
                    .map { premises ->
                        premises.addActors(actors.filter { actor -> actor.premisesId == premises.premisesId })
                    }
            }
    }
}

fun createBoard(): Board {
    return Board(
        modelNo = "GS4GTA25WF",
        feeds = emptyList(),
        widgets = emptyList(),
        rules = emptyList()
    )
}