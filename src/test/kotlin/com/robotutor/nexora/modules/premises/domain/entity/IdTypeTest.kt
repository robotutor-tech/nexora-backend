package com.robotutor.nexora.modules.premises.domain.entity

import com.robotutor.nexora.context.premises.infrastructure.persistence.document.IdType
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IdTypeTest {
    @Test
    fun `PREMISE_ID should have length 8`() {
        IdType.PREMISE_ID.length shouldBe 8
    }

    @Test
    fun `should have correct enum name`() {
        IdType.PREMISE_ID.name shouldBe "PREMISE_ID"
    }
}
