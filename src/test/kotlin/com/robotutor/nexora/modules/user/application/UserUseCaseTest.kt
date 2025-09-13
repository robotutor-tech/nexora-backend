package com.robotutor.nexora.modules.user.application

import com.robotutor.nexora.modules.user.application.command.GetUserCommand
import com.robotutor.nexora.modules.user.builder.UserBuilder
import com.robotutor.nexora.modules.user.domain.exception.NexoraError
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.testUtils.assertErrorWith
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

class UserUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val userUseCase = UserUseCase(userRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should return user from repository when getUser is called`() {
        val userId = UserId("userId123")
        val user = UserBuilder(userId = userId).build()
        val command = GetUserCommand(userId)
        every { userRepository.findByUserId(any()) } returns Mono.just(user)

        val result = userUseCase.getUser(command)
        assertNextWith(result) {
            it shouldBe user
        }
        verify(exactly = 1) {
            userRepository.findByUserId(userId)
        }
    }

    @Test
    fun `should propagate error from repository when getUser fails`() {
        val userId = UserId("userId123")
        val command = GetUserCommand(userId)
        val error = RuntimeException("User not found")
        every { userRepository.findByUserId(userId) } returns Mono.error(error)

        val result = userUseCase.getUser(command)
        assertErrorWith(result) {
            it shouldBe error
        }
        verify(exactly = 1) { userRepository.findByUserId(userId) }
    }

    @Test
    fun `should throw DataNotFoundException with NEXORA0202 when user not found`() {
        val userId = UserId("userId123")
        val command = GetUserCommand(userId)
        every { userRepository.findByUserId(userId) } returns Mono.empty()

        val result = userUseCase.getUser(command)
        assertErrorWith(result) {
            it shouldBe DataNotFoundException(NexoraError.NEXORA0202)
        }
        verify(exactly = 1) {
            userRepository.findByUserId(userId)
        }
    }
}
