package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.common.security.application.ports.TokenValidator
import com.robotutor.nexora.common.security.application.strategy.factory.TokenDataRetrieverStrategyFactory
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.InternalData
import com.robotutor.nexora.shared.domain.model.PrincipalData
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.common.security.domain.model.ValidateTokenResult
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.model.DeviceData
import com.robotutor.nexora.shared.domain.model.InternalContext
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.infrastructure.jackson.DefaultSerializer.serialize
import com.robotutor.nexora.shared.infrastructure.webclient.controllers.ExceptionHandlerRegistry
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.ReactiveContext.putPremisesId
import com.robotutor.nexora.shared.logger.ReactiveContext.putTraceId
import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
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
import java.time.Instant
import java.util.UUID.randomUUID

const val TRACE_ID = "x-trace-id"
const val PREMISES_ID = "x-premises-id"
const val START_TIME = "startTime"

@Component
@Order(2)
class AuthFilter(
    private val routeValidator: RouteValidator,
    private val appConfig: AppConfig,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val tokenValidator: TokenValidator,
    private val tokenDataRetrieverStrategyFactory: TokenDataRetrieverStrategyFactory
) : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return authorize(exchange)
            .contextWrite { writeContextOnChain(it, exchange) }
            .flatMap { tokenResult ->
                tokenDataRetrieverStrategyFactory.getStrategy(tokenResult.principalType)
                    .getPrincipalData(tokenResult.principal)
            }
            .flatMap { principalData ->
                val authenticationToken = UsernamePasswordAuthenticationToken("auth", null, listOf())
                val content = SecurityContextImpl(authenticationToken)
                chain.filter(exchange)
                    .contextWrite { writeContextOnChain(setContextForResolvers(principalData, it, exchange), exchange) }
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


    private fun authorize(exchange: ServerWebExchange): Mono<ValidateTokenResult> {
        val authHeader = exchange.request.headers.getFirst(AUTHORIZATION)

        if (routeValidator.isUnsecured(exchange.request) || authHeader == appConfig.internalAccessToken) {
            return createMono(
                ValidateTokenResult(
                    isValid = true,
                    principalType = TokenPrincipalType.INTERNAL,
                    principal = InternalContext(""),
                    expiresAt = Instant.MAX,
                )
            )
        }

        if (authHeader.isNullOrBlank()) return createMonoError(UnAuthorizedException(NexoraError.NEXORA0101))

        return tokenValidator.validate(authHeader)
    }

    private fun setContextForResolvers(
        principalData: PrincipalData,
        context: Context,
        exchange: ServerWebExchange
    ): Context {
        return when (principalData) {
            is UserData -> context.put(UserData::class.java, principalData)
            is InternalData -> context.put(InternalData::class.java, principalData)
            is InvitationData -> context.put(InvitationData::class.java, principalData)
            is DeviceData -> context.put(DeviceData::class.java, principalData)
            is ActorData -> setContextForResolvers(
                principalData.principal,
                context.put(ActorData::class.java, principalData),
                exchange
            )
        }
            .put(PrincipalData::class.java, principalData)
    }
}

fun getTraceIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[TRACE_ID] as? String
        ?: exchange.request.headers.getFirst(TRACE_ID)
        ?: randomUUID().toString()
}

fun getPremisesIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[PREMISES_ID] as? String
        ?: exchange.request.headers.getFirst(PREMISES_ID)
        ?: "missing-premises-id"
}


fun writeContextOnChain(context: Context, exchange: ServerWebExchange): Context {
    val traceId = getTraceIdFromExchange(exchange)
    val premisesId = getPremisesIdFromExchange(exchange)
    val startTime = exchange.getAttribute(START_TIME) ?: Instant.now()
    val newContext = putTraceId(context, traceId)
    return putPremisesId(newContext, premisesId)
        .put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange))
        .put(START_TIME, startTime)
}