package com.robotutor.nexora.common.utils

import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Small utility to evaluate SpEL expressions against a method invocation.
 *
 * Supported variables:
 * - #{p0} / #{a0} ... for indexed args
 * - #{args} for full args array
 * - named parameters if Kotlin compiler is configured with `javaParameters = true`
 */
@Component
class MethodArgumentExpressionEvaluator {
    fun evaluate(method: Method, args: Array<Any>, expression: String): Any? {
        val context = createContext(args, method)
        return ExpressionParser(expression).getValue(context)
    }

    private fun createContext(args: Array<Any>, method: Method): EvaluationContext {
        val context = EvaluationContext().put("args", args)
        args.forEachIndexed { index, value ->
            context.put("p$index", value).put("a$index", value)
        }
        method.parameters.forEachIndexed { index, parameter ->
            context.put(parameter.name, args[index])
        }
        return context
    }
}

