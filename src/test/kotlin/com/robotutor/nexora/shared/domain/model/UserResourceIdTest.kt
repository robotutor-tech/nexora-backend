package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.context.user.domain.vo.UserId
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserResourceIdTest {
    @Test
    fun `should create UserId with any string`() {
        val userId = UserId("user-123")
        userId.value shouldBe "user-123"
    }

    @Test
    fun `should allow empty string`() {
        val userId = UserId("")
        userId.value shouldBe ""
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
