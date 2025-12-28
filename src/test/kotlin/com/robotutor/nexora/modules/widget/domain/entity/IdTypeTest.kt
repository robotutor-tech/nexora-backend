package com.robotutor.nexora.modules.widget.domain.entity

import com.robotutor.nexora.modules.automation.domain.entity.IdType
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IdTypeTest {
    @Test
    fun `RULE_ID should have length 12`() {
        IdType.RULE_ID.length shouldBe 12
    }

    @Test
    fun `should have correct enum name`() {
        IdType.RULE_ID.name shouldBe "RULE_ID"
    }
}
