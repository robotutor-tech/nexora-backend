package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.shared.domain.vo.ResourceId
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Resolves a ResourceId from a selector expression on application/service methods.
 *
 * Supported selectors:
 * - "ALL" -> ResourceId.ALL
 * - SpEL expressions like "#command.deviceId" or "#p0.deviceId".
 */
@Component
class MethodArgumentSpringExpressionResourceIdResolver(
    private val parser: ExpressionParser = SpelExpressionParser(),
) {
    private val nameDiscoverer = DefaultParameterNameDiscoverer()

    fun resolve(method: Method, args: Array<Any?>, selector: String): ResourceId {
        if (selector.isBlank() || selector == "ALL") {
            return ResourceId.ALL
        }

        val context = StandardEvaluationContext().apply {
            // indexed variables: #p0 / #a0
            args.forEachIndexed { index, value ->
                setVariable("p$index", value)
                setVariable("a$index", value)
            }
            setVariable("args", args)

            // named parameters: #command, #actorData, etc.
            val names = nameDiscoverer.getParameterNames(method).orEmpty()
            names.forEachIndexed { index, name ->
                if (name.isNotBlank()) setVariable(name, args.getOrNull(index))
            }
        }

        val result = parser.parseExpression(selector).getValue(context)
            ?: throw IllegalArgumentException("Unable to resolve resourceId from selector '$selector' for handler ${method.declaringClass.simpleName}.${method.name}")

        return when (result) {
            is ResourceId -> result
            else -> {
                val value = result.toString().trim()
                if (value.isBlank()) {
                    throw IllegalArgumentException("Resolved resourceId is blank for selector '$selector' for handler ${method.declaringClass.simpleName}.${method.name}")
                }
                ResourceId(value)
            }
        }
    }
}

