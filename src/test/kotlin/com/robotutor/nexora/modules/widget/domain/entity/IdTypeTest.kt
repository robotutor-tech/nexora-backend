package com.robotutor.nexora.modules.widget.domain.entity

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IdTypeTest {
    @Test
    fun `WIDGET_ID should have length 12`() {
        IdType.WIDGET_ID.length shouldBe 12
    }

    @Test
    fun `should have correct enum name`() {
        IdType.WIDGET_ID.name shouldBe "WIDGET_ID"
    }
}

