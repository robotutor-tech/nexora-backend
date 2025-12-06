package com.robotutor.nexora.orchestration.controller

import com.robotutor.nexora.orchestration.client.view.UserResponse
import com.robotutor.nexora.orchestration.controller.view.UserRegistrationRequest
import com.robotutor.nexora.orchestration.workflow.UserRegistrationWorkflow
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestration")
class UserRegistrationController(
    private val userRegistrationWorkflow: UserRegistrationWorkflow,
) {
    @PostMapping("/users/register")
    fun registerUser(@RequestBody @Validated user: UserRegistrationRequest): Mono<UserResponse> {
        return userRegistrationWorkflow.registerUser(user)
    }
}