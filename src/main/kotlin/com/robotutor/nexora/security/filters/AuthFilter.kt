package com.robotutor.nexora.security.filters

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.models.ServerWebExchangeDTO
import com.robotutor.nexora.logger.serializer.DefaultSerializer.serialize
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.gateway.AuthGateway
import com.robotutor.nexora.security.models.*
import com.robotutor.nexora.webClient.exceptions.BaseException
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
import java.time.LocalDateTime

@Component
@Order(2)
class AuthFilter(
    private val routeValidator: RouteValidator,
    private val authGateway: AuthGateway,
    private val appConfig: AppConfig
) : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        return authorize(exchange)
            .flatMap { authenticationData ->
                val authenticationToken = UsernamePasswordAuthenticationToken("authenticationData", null, listOf())
                val content = SecurityContextImpl(authenticationToken)
                chain.filter(exchange)
                    .contextWrite { writeContext(authenticationData, it) }
                    .contextWrite { it.put("startTime", startTime) }
                    .contextWrite { it.put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange)) }
                    .contextWrite { ReactiveSecurityContextHolder.withSecurityContext(createMono(content)) }
            }
            .onErrorResume {
                val response = exchange.response
                val error = try {
                    (it as BaseException).errorResponse()
                } catch (_: Exception) {
                    mapOf("errorCode" to "", "message" to it.message)
                }
                val content = response.bufferFactory().wrap(serialize(error).toByteArray())
                response.writeWith(createMono(content))
            }
            .contextWrite { it.put("startTime", startTime) }
            .contextWrite { it.put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange)) }
    }


    private fun authorize(exchange: ServerWebExchange): Mono<IAuthenticationData> {
        val authHeader = exchange.request.headers.getFirst(AUTHORIZATION)
        if (routeValidator.isUnsecured(exchange.request) || authHeader == appConfig.internalAccessToken) {
            return createMono(InternalUserData("00000000"))
        }
        return authGateway.validate(exchange)
    }

    private fun writeContext(authenticationData: IAuthenticationData, context: Context): Context {
        return when (authenticationData) {
            is PremisesActorData -> {
                val premisesActorData = authenticationData.authenticationData
                val premisesContext = when (authenticationData.authenticationData) {
                    is AuthUserData -> context.put(AuthUserData::class.java, premisesActorData)
                    is DeviceData -> context.put(DeviceData::class.java, premisesActorData)
                    is ServerData -> context.put(ServerData::class.java, premisesActorData)
                    else -> context
                }
                premisesContext.put(PremisesActorData::class.java, authenticationData)
            }

            is AuthUserData -> context.put(AuthUserData::class.java, authenticationData)
            is InvitationData -> context.put(InvitationData::class.java, authenticationData)

            else -> context
        }
    }
}

