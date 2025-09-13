package com.robotutor.nexora.shared.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MobileTest {
    @Test
    fun `should create Mobile with valid 10 digit number`() {
        val mobile = Mobile("9876543210")
        mobile.value shouldBe "9876543210"
    }

    @Test
    fun `should throw exception for less than 10 digits`() {
        shouldThrow<IllegalArgumentException> {
            Mobile("123456789")
        }.message shouldBe "Invalid mobile number format: must be exactly 10 digits (Indian mobile)"
    }

    @Test
    fun `should throw exception for more than 10 digits`() {
        shouldThrow<IllegalArgumentException> {
            Mobile("12345678901")
        }.message shouldBe "Invalid mobile number format: must be exactly 10 digits (Indian mobile)"
    }

    @Test
    fun `should throw exception for non-numeric input`() {
        shouldThrow<IllegalArgumentException> {
            Mobile("abcdefghij")
        }.message shouldBe "Invalid mobile number format: must be exactly 10 digits (Indian mobile)"
    }

    @Test
    fun `should throw exception for empty string`() {
        shouldThrow<IllegalArgumentException> {
            Mobile("")
        }.message shouldBe "Invalid mobile number format: must be exactly 10 digits (Indian mobile)"
    }

    @Test
    fun `should allow mobile number with leading zeros`() {
        val mobile = Mobile("0123456789")
        mobile.value shouldBe "0123456789"
    }
}

