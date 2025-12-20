package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.client.UserClient
import com.robotutor.nexora.orchestration.client.view.AccountPayload
import com.robotutor.nexora.orchestration.client.view.UserResponse
import com.robotutor.nexora.orchestration.controller.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.messaging.event.CompensateAccountRegistrationEvent
import com.robotutor.nexora.orchestration.messaging.event.OrchestrationEvent
import com.robotutor.nexora.shared.domain.event.EventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserRegistrationWorkflow(
    private val userClient: UserClient,
    private val iamClient: IAMClient,
    private val eventPublisher: EventPublisher<OrchestrationEvent>,
) {
    fun registerUser(user: UserRegistrationRequest): Mono<UserResponse> {
        val payload = AccountPayload(
            user.email,
            user.password,
            "PASSWORD",
            "HUMAN"
        )
        return iamClient.registerAccount(payload)
            .flatMap { iamAccount ->
                userClient.registerUser(user, iamAccount)
                    .onErrorResume { throwable ->
                        eventPublisher.publish(CompensateAccountRegistrationEvent(iamAccount.accountId), throwable)
                    }
            }
    }
}
