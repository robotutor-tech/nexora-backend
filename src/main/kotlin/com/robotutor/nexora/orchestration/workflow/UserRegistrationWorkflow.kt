package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.client.UserClient
import com.robotutor.nexora.orchestration.client.view.UserResponse
import com.robotutor.nexora.orchestration.controller.view.UserRegistrationRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserRegistrationWorkflow(private val userClient: UserClient, private val iamClient: IAMClient) {
    fun registerUser(user: UserRegistrationRequest): Mono<UserResponse> {
        return userClient.registerUser(user)
            .flatMap { userResponse ->
                iamClient.registerIdentity(userResponse, user.password)
                    .map { userResponse }
            }
    }
}
