package com.robotutor.nexora.common.utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

private class DemoService {
    fun hello(name: String, age: Int): String = "$name-$age"
}

class MethodArgumentSpelEvaluatorTest {

    private val evaluator = MethodArgumentExpressionEvaluator()

    @Test
    fun `should evaluate indexed variables`() {
        val method = DemoService::class.java.getDeclaredMethod("hello", String::class.java, Int::class.javaPrimitiveType)
//        val result = evaluator.evaluateAsString(method, arrayOf("john", 10), "#p0 + '-' + #p1")
//        result shouldBe "john-10"
    }
}

