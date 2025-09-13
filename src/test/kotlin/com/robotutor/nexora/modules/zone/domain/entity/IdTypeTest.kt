package com.robotutor.nexora.modules.zone.domain.entity

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IdTypeTest {
    @Test
    fun `ZONE_ID should have length 8`() {
        IdType.ZONE_ID.length shouldBe 8
    }

    @Test
    fun `should have correct enum name`() {
        IdType.ZONE_ID.name shouldBe "ZONE_ID"
    }
}

