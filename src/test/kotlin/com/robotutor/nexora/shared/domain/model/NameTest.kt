package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.shared.domain.exception.BadDataException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NameTest {
    @Test
    fun `should create Name with valid length and trim whitespace`() {
        val name = Name("   John Doe   ")
        name.value shouldBe "   John Doe   "
    }

    @Test
    fun `should throw exception for name shorter than 4 characters after trim`() {
        val exception = assertThrows<BadDataException> {
            Name("  Joe  ")
        }
        exception.message shouldBe "Name must be between 4 and 30 characters long"
    }

    @Test
    fun `should throw exception for name longer than 30 characters after trim`() {
        val longName = "A".repeat(31)
        val exception = assertThrows<BadDataException> {
            Name("  $longName  ")
        }
        exception.message shouldBe "Name must be between 4 and 30 characters long"
    }

    @Test
    fun `should allow name with exactly 4 characters after trim`() {
        val name = Name(" John ")
        name.value shouldBe " John "
    }

    @Test
    fun `should allow name with exactly 30 characters after trim`() {
        val name = Name("  ${"A".repeat(30)}  ")
        name.value shouldBe "  ${"A".repeat(30)}  "
    }

    @Test
    fun `should allow special characters within valid length after trim`() {
        val name = Name(" J@hn_Doe! ")
        name.value shouldBe " J@hn_Doe! "
    }

    @Test
    fun `should be equal for same value`() {
        Name("John Doe") shouldBe Name("John Doe")
    }

    @Test
    fun `should throw exception for empty name`() {
        val exception = assertThrows<BadDataException> {
            Name("")
        }
        exception.message shouldBe "Name must be between 4 and 30 characters long"
    }

    @Test
    fun `should throw exception for whitespace only name`() {
        val exception = assertThrows<BadDataException> {
            Name("    ")
        }
        exception.message shouldBe "Name must be between 4 and 30 characters long"
    }
}

