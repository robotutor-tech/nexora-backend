package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.common.http.controllers.ExceptionHandlerRegistry
import com.robotutor.nexora.common.security.application.ports.SessionValidator
import com.robotutor.nexora.common.security.application.writeContextOnChain
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.domain.exception.SecurityError
import com.robotutor.nexora.common.security.domain.vo.SessionValidationResult
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer.serialize
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.ReactiveContext.X_PREMISES_ID
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.principal.InternalData
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context


@Component
@Order(2)
class AuthFilter(
    private val routeValidator: RouteValidator,
    private val appConfig: AppConfig,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val sessionValidator: SessionValidator,
) : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return authorize(exchange)
            .contextWrite { writeContextOnChain(it, exchange) }
            .flatMap { tokenResult ->
                val authenticationToken = UsernamePasswordAuthenticationToken("auth", null, listOf())
                val content = SecurityContextImpl(authenticationToken)
                chain.filter(exchange)
                    .contextWrite { setContextForResolvers(tokenResult.principalData, it, exchange) }
                    .contextWrite { ReactiveSecurityContextHolder.withSecurityContext(createMono(content)) }
            }
            .onErrorResume {
                val responseEntity = exceptionHandlerRegistry.handle(it)
                val response = exchange.response
                exchange.response.statusCode = responseEntity.statusCode
                val content = response.bufferFactory().wrap(serialize(responseEntity.body).toByteArray())
                response.writeWith(createMono(content))
            }
    }


    private fun authorize(exchange: ServerWebExchange): Mono<SessionValidationResult> {
        val authHeader = exchange.request.headers.getFirst(AUTHORIZATION)

        if (routeValidator.isUnsecured(exchange.request) || authHeader == "Bearer ${appConfig.internalAccessToken}") {
            return createMono(
                SessionValidationResult(
                    isValid = true,
                    principalData = InternalData(appConfig.internalAccessToken),
                    expiresIn = 300,
                )
            )
        }

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return createMonoError(UnAuthorizedException(SecurityError.NEXORA0101))
        }

        return sessionValidator.validate(authHeader)
    }

    private fun setContextForResolvers(
        principalData: PrincipalData,
        context: Context,
        exchange: ServerWebExchange
    ): Context {
        val newContext = when (principalData) {
            is AccountData -> context.put(AccountData::class.java, principalData)
            is InternalData -> context.put(InternalData::class.java, principalData)
            is ActorData -> {
                exchange.attributes[X_PREMISES_ID] = principalData.premisesId.value
                var newContext = context.put(ActorData::class.java, principalData)
                newContext = newContext.put(AccountData::class.java, principalData)
                newContext
            }
        }
        return writeContextOnChain(newContext, exchange)
    }
}
