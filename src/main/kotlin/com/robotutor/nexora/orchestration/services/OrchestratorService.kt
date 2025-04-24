package com.robotutor.nexora.orchestration.services

import com.robotutor.nexora.orchestration.controllers.view.PremisesRegistrationRequest
import com.robotutor.nexora.orchestration.controllers.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.gateway.AuthGateway
import com.robotutor.nexora.orchestration.gateway.IAMGateway
import com.robotutor.nexora.orchestration.gateway.PremisesGateway
import com.robotutor.nexora.orchestration.gateway.UserGateway
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrchestratorService(
    private val userGateway: UserGateway,
    private val authGateway: AuthGateway,
    private val premisesGateway: PremisesGateway,
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
        // TODO: Create roles and policies with current user
    }
}
