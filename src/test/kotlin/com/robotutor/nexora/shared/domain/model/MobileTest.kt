package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.shared.domain.exception.BadDataException
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
        shouldThrow<BadDataException> {
            Mobile("123456789")
        }.message shouldBe "Mobile must be valid"
    }

    @Test
    fun `should throw exception for more than 10 digits`() {
        shouldThrow<BadDataException> {
            Mobile("12345678901")
        }.message shouldBe "Mobile must be valid"
    }

    @Test
    fun `should throw exception for non-numeric input`() {
        shouldThrow<BadDataException> {
            Mobile("abcdefghij")
        }.message shouldBe "Mobile must be valid"
    }

    @Test
    fun `should throw exception for empty string`() {
        shouldThrow<BadDataException> {
            Mobile("")
        }.message shouldBe "Mobile must be valid"
    }

    @Test
    fun `should allow mobile number with leading zeros`() {
        val mobile = Mobile("0123456789")
        mobile.value shouldBe "0123456789"
    }
}
