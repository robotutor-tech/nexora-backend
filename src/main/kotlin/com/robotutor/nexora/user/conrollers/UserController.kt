package com.robotutor.nexora.user.conrollers

import com.robotutor.nexora.user.conrollers.views.UserRequest
import com.robotutor.nexora.user.conrollers.views.UserView
import com.robotutor.nexora.user.services.UserService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated user: UserRequest): Mono<UserView> {
        return userService.register(user).map { UserView.from(it) }
    }
}