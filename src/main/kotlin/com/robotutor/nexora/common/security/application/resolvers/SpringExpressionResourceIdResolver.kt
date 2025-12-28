package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ResourceId
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.server.ServerWebExchange

@Component
class SpringExpressionResourceIdResolver {

    private val parser: ExpressionParser = SpelExpressionParser()

    fun resolve(httpAuthorize: HttpAuthorize, exchange: ServerWebExchange, handler: HandlerMethod): ResourceId {
        val selector = httpAuthorize.selector
        if (selector == "ALL") return ResourceId.ALL

        val pathVars: Map<String, String> = exchange.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
            ?: emptyMap()

        val query: Map<String, String> = exchange.request.queryParams
            .toSingleValueMap()

        val headers: Map<String, String> = exchange.request.headers
            .toSingleValueMap()

        if (selector.startsWith("#") && selector.matches(Regex("#[a-zA-Z_][a-zA-Z0-9_]*"))) {
            val key = selector.removePrefix("#")
            val value = pathVars[key]
            if (!value.isNullOrBlank()) return ResourceId(value)
        }

        val expression = parser.parseExpression(selector)

        val context = StandardEvaluationContext().apply {
            setVariable("pathVars", pathVars)
            setVariable("query", query)
            setVariable("headers", headers)
            setVariable("handler", handler)
        }

        val raw = expression.getValue(context)
        val resolved = when (raw) {
            null -> null
            is ResourceId -> raw.value
            else -> raw.toString()
        }?.trim()

        validation(resolved.isNullOrBlank()) {
            "Unable to resolve resourceId from selector '$selector' for handler ${handler.beanType.simpleName}.${handler.method.name}. " +
                "Available now: pathVars=${pathVars.keys}, query=${query.keys}, headers=${headers.keys}"
        }

        return ResourceId(resolved!!)
    }
}
