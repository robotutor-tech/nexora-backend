package com.robotutor.nexora.modules.user.domain.exception

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NexoraErrorTest {
    @Test
    fun `NEXORA0201 should have correct errorCode and message`() {
        NexoraError.NEXORA0201.errorCode shouldBe "NEXORA-0201"
        NexoraError.NEXORA0201.message shouldBe "User already registered with this email."
    }

    @Test
    fun `should have correct enum name`() {
        NexoraError.NEXORA0201.name shouldBe "NEXORA0201"
    }
}

