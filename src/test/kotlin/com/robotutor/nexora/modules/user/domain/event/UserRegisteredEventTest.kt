package com.robotutor.nexora.modules.user.domain.event

import com.robotutor.nexora.shared.domain.model.UserId
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserRegisteredEventTest {
    @Test
    fun `should create UserRegisteredEvent with correct userId`() {
        val userId = UserId("user-123")
        val event = UserRegisteredEvent(userId)
        event.userId shouldBe userId
    }

    @Test
    fun `should be equal for same userId`() {
        val userId = UserId("user-123")
        val event1 = UserRegisteredEvent(userId)
        val event2 = UserRegisteredEvent(userId)
        event1 shouldBe event2
    }
}
