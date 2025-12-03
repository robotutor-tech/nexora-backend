package com.robotutor.nexora.modules.user

import com.robotutor.nexora.TEST_TOKEN
import com.robotutor.nexora.annotation.IntegrationTest
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.TOKEN_COLLECTION
import com.robotutor.nexora.modules.user.builder.TokenDocumentBuilder
import com.robotutor.nexora.modules.user.builder.UserDocumentBuilder
import com.robotutor.nexora.context.user.domain.exception.NexoraError
import com.robotutor.nexora.context.user.infrastructure.persistence.document.USER_COLLECTION
import com.robotutor.nexora.context.user.interfaces.dto.UserResponse
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant


@IntegrationTest
class UserIntegrationTest(
    @param:Autowired private val webTestClient: WebTestClient,
    @param:Autowired private val reactiveMongoTemplate: ReactiveMongoTemplate,
) {
    @BeforeEach
    fun setUp() {
        reactiveMongoTemplate.dropCollection(USER_COLLECTION).block()
        clearAllMocks()
        val tokenDocument = TokenDocumentBuilder(
            value = TEST_TOKEN,
            expiresAt = Instant.now().plusSeconds(3600)
        ).build()
        reactiveMongoTemplate.save(tokenDocument).block()
        val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")
        mockkStatic(Instant::class)
        every { Instant.now() } returns fixedInstant
    }

    @AfterEach
    fun tearDown() {
        reactiveMongoTemplate.dropCollection(USER_COLLECTION).block()
        reactiveMongoTemplate.dropCollection(TOKEN_COLLECTION).block()
        clearAllMocks()
    }

    @Test
    fun `should get user by userId`() {
        val userDocument = UserDocumentBuilder(userId = "userId").build()
        reactiveMongoTemplate.save(userDocument).block()

        webTestClient.get()
            .uri("/users/userId")
            .header("Authorization", "Bearer $TEST_TOKEN")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(UserResponse::class.java)
            .returnResult()
            .responseBody!! shouldBe UserResponse(
            userId = "userId",
            name = "John",
            email = "example@email.com",
            mobile = "9012345678",
            isEmailVerified = false,
            isMobileVerified = false,
            registeredAt = Instant.parse("2023-01-01T00:00:00Z"),
        )
    }

    @Test
    fun `should register a new user`() {
        val requestBody = mapOf(
            "name" to "Alice",
            "email" to "alice@email.com",
            "mobile" to "9123456789",
            "password" to "Password@1",
        )

        webTestClient.post()
            .uri("/users")
            .bodyValue(requestBody)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(UserResponse::class.java)
            .returnResult()
            .responseBody!! shouldBe UserResponse(
            userId = "0000000001",
            name = "Alice",
            email = "alice@email.com",
            mobile = "9123456789",
            isEmailVerified = false,
            isMobileVerified = false,
            registeredAt = Instant.parse("2023-01-01T00:00:00Z"),
        )
    }

    @Test
    fun `should not register a new user if user already exists`() {
        val user1 = UserDocumentBuilder(
            userId = "userId",
            name = "John",
            email = "alice@email.com",
            mobile = "9000000001"
        ).build()
        reactiveMongoTemplate.save(user1).block()

        val requestBody = mapOf(
            "name" to "Alice",
            "email" to "alice@email.com",
            "mobile" to "9123456789",
            "password" to "Password@1",
        )

        webTestClient.post()
            .uri("/users")
            .bodyValue(requestBody)
            .exchange()
            .expectStatus()
            .is4xxClientError
            .expectBody(ErrorResponse::class.java)
            .returnResult()
            .responseBody!! shouldBe ErrorResponse(NexoraError.NEXORA0201.errorCode, NexoraError.NEXORA0201.message)
    }

    @Test
    fun `should get me as authenticated user`() {
        val user1 = UserDocumentBuilder(
            userId = "userId",
            name = "John",
            email = "john@email.com",
            mobile = "9000000001"
        ).build()
        reactiveMongoTemplate.save(user1).block()

        webTestClient.get()
            .uri("/users/me")
            .header("Authorization", "Bearer $TEST_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserResponse::class.java)
            .returnResult()
            .responseBody!! shouldBe UserResponse(
            userId = "userId",
            name = "John",
            email = "john@email.com",
            mobile = "9000000001",
            isEmailVerified = false,
            isMobileVerified = false,
            registeredAt = Instant.parse("2023-01-01T00:00:00Z"),
        )
    }
}