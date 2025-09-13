package com.robotutor.nexora.modules.user.interfaces.controller

import com.robotutor.nexora.modules.auth.domain.entity.Password
import com.robotutor.nexora.modules.user.application.RegisterUserUseCase
import com.robotutor.nexora.modules.user.application.UserUseCase
import com.robotutor.nexora.modules.user.application.command.GetUserCommand
import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.builder.UserBuilder
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserRequest
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserResponse
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.testUtils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.Instant

class UserControllerTest {
    val mockUserUseCase = mockk<UserUseCase>()
    val mockRegisterUserUseCase = mockk<RegisterUserUseCase>()
    val userController = UserController(registerUserUseCase = mockRegisterUserUseCase, userUseCase = mockUserUseCase)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should register a user`() {
        val user = UserBuilder().build()
        every { mockRegisterUserUseCase.register(any()) } returns Mono.just(user)

        val userRequest = UserRequest("John", "example@email.com", "9012345678", "12345678")
        val userResult = userController.register(userRequest)

        assertNextWith(userResult) {
            it shouldBe UserResponse(
                userId = "userId",
                name = "John",
                email = "example@email.com",
                mobile = "9012345678",
                isEmailVerified = false,
                isMobileVerified = false,
                registeredAt = Instant.parse("2023-01-01T00:00:00Z"),
            )
            verify(exactly = 1) {
                mockRegisterUserUseCase.register(
                    RegisterUserCommand(
                        email = Email("example@email.com"),
                        password = Password("12345678"),
                        name = Name("John"),
                        mobile = Mobile("9012345678")
                    )
                )
            }
        }
    }

    @Test
    fun `should return current user via me`() {
        val user = UserBuilder().build()
        every { mockUserUseCase.getUser(any()) } returns Mono.just(user)

        val userData = UserData(
            userId = UserId("userId"),
            name = Name("ignored"),
            email = Email("ignored@example.com"),
            registeredAt = Instant.parse("2020-01-01T00:00:00Z")
        )

        val result = userController.me(userData)

        assertNextWith(result) {
            it shouldBe UserResponse(
                userId = "userId",
                name = "John",
                email = "example@email.com",
                mobile = "9012345678",
                isEmailVerified = false,
                isMobileVerified = false,
                registeredAt = Instant.parse("2023-01-01T00:00:00Z"),
            )
            verify(exactly = 1) {
                mockUserUseCase.getUser(GetUserCommand(UserId("userId")))
            }
        }
    }

    @Test
    fun `should return user by id via getUser`() {
        val user = UserBuilder().build()
        every { mockUserUseCase.getUser(any()) } returns Mono.just(user)

        val result = userController.getUser("userId")

        assertNextWith(result) {
            it shouldBe UserResponse(
                userId = "userId",
                name = "John",
                email = "example@email.com",
                mobile = "9012345678",
                isEmailVerified = false,
                isMobileVerified = false,
                registeredAt = Instant.parse("2023-01-01T00:00:00Z"),
            )
            verify(exactly = 1) {
                mockUserUseCase.getUser(GetUserCommand(UserId("userId")))
            }
        }
    }
}