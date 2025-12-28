package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.shared.domain.exception.BadDataException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EmailTest {
    @Test
    fun `should create Email with valid email`() {
        val email = Email("test@example.com")
        email.value shouldBe "test@example.com"
    }

    @Test
    fun `should allow uppercase and plus in email`() {
        val email = Email("USER+test@DOMAIN.COM")
        email.value shouldBe "USER+test@DOMAIN.COM"
    }

    @Test
    fun `should throw exception for missing at symbol`() {
        shouldThrow<BadDataException> {
            Email("test.example.com")
        }.message shouldBe "Email must be valid"
    }

    @Test
    fun `should throw exception for missing domain`() {
        shouldThrow<BadDataException> {
            Email("test@.com")
        }.message shouldBe "Email must be valid"
    }

    @Test
    fun `should throw exception for missing TLD`() {
        shouldThrow<BadDataException> {
            Email("test@example")
        }.message shouldBe "Email must be valid"
    }

    @Test
    fun `should throw exception for empty string`() {
        shouldThrow<BadDataException> {
            Email("")
        }.message shouldBe "Email must be valid"
    }

    @Test
    fun `should throw exception for spaces in email`() {
        shouldThrow<BadDataException> {
            Email("test@ example.com")
        }.message shouldBe "Email must be valid"
    }

    @Test
    fun `should be equal for same value`() {
        Email("test@example.com") shouldBe Email("test@example.com")
    }
}
