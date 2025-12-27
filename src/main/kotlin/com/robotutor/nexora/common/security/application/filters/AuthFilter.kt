package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.common.security.application.ports.SessionValidator
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.application.writeContextOnChain
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import com.robotutor.nexora.common.security.domain.vo.InternalPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.SessionValidationResult
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.InternalData
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.infrastructure.serializer.DefaultSerializer.serialize
import com.robotutor.nexora.shared.infrastructure.webclient.controllers.ExceptionHandlerRegistry
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
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
                    .contextWrite {
                        val principalData = tokenResult.principal.toPrincipalData()
                        writeContextOnChain(setContextForResolvers(principalData, it, exchange), exchange)
                    }
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
                    principal = InternalPrincipalContext(appConfig.internalAccessToken),
                    expiresIn = 300,
                )
            )
        }

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return createMonoError(UnAuthorizedException(NexoraError.NEXORA0101))
        }

        return sessionValidator.validate(authHeader)
    }

    private fun setContextForResolvers(
        principalData: PrincipalData,
        context: Context,
        exchange: ServerWebExchange
    ): Context {
        return when (principalData) {
            is AccountData -> context.put(AccountData::class.java, principalData)
            is InternalData -> context.put(InternalData::class.java, principalData)
            is ActorData -> context.put(ActorData::class.java, principalData)
                .put(AccountData::class.java, AccountData(principalData.accountId, principalData.type))
        }
            .put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange))
    }
}

