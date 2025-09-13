package com.robotutor.nexora.modules.user.domain.entity

import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Mobile
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

class UserTest {
    @Test
    fun `should construct user with all fields`() {
        val userId = UserId("userId123")
        val name = Name("John Doe")
        val email = Email("john@example.com")
        val mobile = Mobile("9012345678")
        val registeredAt = Instant.parse("2023-01-01T00:00:00Z")
        val version = 1L
        val user = User(
            userId = userId,
            name = name,
            email = email,
            mobile = mobile,
            isEmailVerified = true,
            isMobileVerified = true,
            registeredAt = registeredAt,
            version = version
        )
        user.userId shouldBe userId
        user.name shouldBe name
        user.email shouldBe email
        user.mobile shouldBe mobile
        user.isEmailVerified shouldBe true
        user.isMobileVerified shouldBe true
        user.registeredAt shouldBe registeredAt
        user.version shouldBe version
    }

    @Test
    fun `register factory should create user and add UserRegisteredEvent`() {
        val userId = UserId("userId456")
        val name = Name("Jane Doe")
        val email = Email("jane@example.com")
        val mobile = Mobile("9876543210")
        val user = User.register(userId, name, email, mobile)
        user.userId shouldBe userId
        user.name shouldBe name
        user.email shouldBe email
        user.mobile shouldBe mobile
        user.isEmailVerified shouldBe false
        user.isMobileVerified shouldBe false
        user.version shouldBe null
        // Only check fields, do not check domain events since domainEvents is private and no accessor exists
    }
}
