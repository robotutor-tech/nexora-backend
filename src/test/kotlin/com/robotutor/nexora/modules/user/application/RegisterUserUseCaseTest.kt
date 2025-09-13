package com.robotutor.nexora.modules.user.application

import com.robotutor.nexora.modules.auth.domain.entity.Password
import com.robotutor.nexora.modules.user.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.application.service.RegisterAuthUser
import com.robotutor.nexora.modules.user.domain.entity.IdType
import com.robotutor.nexora.modules.user.domain.entity.User
import com.robotutor.nexora.modules.user.domain.event.UserEvent
import com.robotutor.nexora.modules.user.domain.exception.NexoraError
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.exception.DuplicateDataException
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.testUtils.assertErrorWith
import com.robotutor.nexora.testUtils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.Instant

class RegisterUserUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val idGeneratorService = mockk<IdGeneratorService>()
    private val registerAuthUser = mockk<RegisterAuthUser>()
    private val eventPublisher = mockk<EventPublisher<UserEvent>>()
    private val registerUserUseCase = RegisterUserUseCase(
        userRepository,
        idGeneratorService,
        registerAuthUser,
        eventPublisher
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        val fixedInstant = Instant.parse("2025-09-12T10:15:30.00Z")
        mockkStatic(Instant::class)
        every { Instant.now() } returns fixedInstant
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should register user successfully`() {
        val email = Email("test@example.com")
        val password = mockk<Password>()
        val name = Name("John Doe")
        val mobile = Mobile("9876543210")
        val userId = UserId("userId123")
        val user = User.register(userId, name, email, mobile)
        val command = RegisterUserCommand(email, password, name, mobile)
        val registerAuthUserCommand = RegisterAuthUserCommand(userId, email, password)

        every { userRepository.findByEmail(any()) } returns Mono.empty()
        every { idGeneratorService.generateId(any(), any<Class<SequenceId>>()) } returns Mono.just(userId)
        every { userRepository.save(any()) } returns Mono.just(user)
        every { registerAuthUser.register(any()) } returns Mono.just(registerAuthUserCommand)
        every { eventPublisher.publish(any()) } returns Mono.just(Unit)

        val result = registerUserUseCase.register(command)

        assertNextWith(result) {
            it shouldBe user
        }
        verify(exactly = 1) {
            idGeneratorService.generateId(IdType.USER_ID, UserId::class.java)
            userRepository.save(any())
            registerAuthUser.register(any())
            userRepository.findByEmail(email)
            eventPublisher.publish(any())
        }
    }

    @Test
    fun `should throw DuplicateDataException when user already exists`() {
        val email = Email("test@example.com")
        val password = Password("Password")
        val name = Name("John Doe")
        val mobile = Mobile("9876543210")
        val userId = UserId("userId123")
        val user = User.register(userId, name, email, mobile)
        val command = RegisterUserCommand(email, password, name, mobile)

        every { userRepository.findByEmail(email) } returns Mono.just(user)
        every { idGeneratorService.generateId(any(), any<Class<SequenceId>>()) } returns Mono.just(userId)

        val result = registerUserUseCase.register(command)

        assertErrorWith(result) {
            it shouldBe DuplicateDataException(NexoraError.NEXORA0201)
        }
        verify(exactly = 1) {
            userRepository.findByEmail(email)
            idGeneratorService.generateId(any(), any<Class<SequenceId>>())
        }
        verify(exactly = 0) {
            userRepository.save(any())
            registerAuthUser.register(any())
            eventPublisher.publish(any())
        }
    }

    @Test
    fun `should delete user and propagate error if auth registration fails`() {
        val email = Email("test@example.com")
        val password = Password("Password")
        val name = Name("John Doe")
        val mobile = Mobile("9876543210")
        val userId = UserId("userId123")
        val user = User.register(userId, name, email, mobile)
        val command = RegisterUserCommand(email, password, name, mobile)
        val authError = RuntimeException("Auth registration failed")

        every { userRepository.findByEmail(any()) } returns Mono.empty()
        every { idGeneratorService.generateId(any(), any<Class<UserId>>()) } returns Mono.just(userId)
        every { userRepository.save(any()) } returns Mono.just(user)
        every { registerAuthUser.register(any()) } returns Mono.error(authError)
        every { userRepository.deleteByUserId(any()) } returns Mono.just(user)

        val result = registerUserUseCase.register(command)
        assertErrorWith(result) {
            it shouldBe authError
        }
        verify(exactly = 1) {
            idGeneratorService.generateId(IdType.USER_ID, UserId::class.java)
            userRepository.save(user)
            registerAuthUser.register(RegisterAuthUserCommand(userId, email, password))
            userRepository.findByEmail(email)
            userRepository.deleteByUserId(userId)
        }
    }
}
