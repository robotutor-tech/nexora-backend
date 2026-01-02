package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.exception.BadDataException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserResourceIdTest {
    @Test
    fun `should create UserId with any string`() {
        val userId = UserId("user-123")
        userId.value shouldBe "user-123"
    }

    @Test
    fun `should throw exception for empty string`() {
        val exception = assertThrows<BadDataException> { UserId("") }
        exception.message shouldBe "User id must not be blank"
    }

    @Test
    fun `should allow special characters`() {
        val userId = UserId("user@domain!")
        userId.value shouldBe "user@domain!"
    }

    @Test
    fun `should be equal for same value`() {
        UserId("user-123") shouldBe UserId("user-123")
    }
}
