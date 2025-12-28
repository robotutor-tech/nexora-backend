package com.robotutor.nexora.common.cache.infrastructure.resolvers

import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class MethodArgumentSpringExpressionCacheKeyResolver(
    private val parser: ExpressionParser = SpelExpressionParser(),
) {
    private val nameDiscoverer = DefaultParameterNameDiscoverer()

    fun resolve(method: Method, args: Array<Any?>, expression: String): String {
        if (expression.isBlank()) {
            throw IllegalArgumentException("Cache key expression must not be blank")
        }

        val context = StandardEvaluationContext().apply {
            args.forEachIndexed { index, value ->
                setVariable("p$index", value)
                setVariable("a$index", value)
            }
            setVariable("args", args)

            val names = nameDiscoverer.getParameterNames(method).orEmpty()
            names.forEachIndexed { index, name ->
                if (name.isNotBlank()) setVariable(name, args.getOrNull(index))
            }
        }

        val result = parser.parseExpression(expression).getValue(context)
            ?: throw IllegalArgumentException("Resolved cache key is null for expression '$expression' on ${method.declaringClass.simpleName}.${method.name}")

        return result.toString().trim().also {
            if (it.isBlank()) {
                throw IllegalArgumentException("Resolved cache key is blank for expression '$expression' on ${method.declaringClass.simpleName}.${method.name}")
            }
        }
    }
}

