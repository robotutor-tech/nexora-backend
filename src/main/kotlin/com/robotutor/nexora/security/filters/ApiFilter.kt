package com.robotutor.nexora.security.filters

import com.robotutor.nexora.webClient.exceptions.UnAuthorizedException
import com.robotutor.nexora.logger.LogDetails
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.models.RequestDetails
import com.robotutor.nexora.logger.models.ResponseDetails
import com.robotutor.nexora.logger.models.ServerWebExchangeDTO
import com.robotutor.nexora.logger.serializer.DefaultSerializer.serialize
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.exceptions.NexoraError
import com.robotutor.nexora.security.gateway.AuthGateway
import com.robotutor.nexora.security.getTraceId
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.DeviceData
import com.robotutor.nexora.security.models.IAuthenticationData
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.ServerData
import com.robotutor.nexora.webClient.exceptions.BaseException
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
import java.time.ZoneOffset

@Component
class ApiFilter(
    private val routeValidator: RouteValidator,
    private val authGateway: AuthGateway,
    private val appConfig: AppConfig
) : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        val additionalDetails = mapOf("method" to exchange.request.method, "path" to exchange.request.uri.path)
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
                response.statusCode = HttpStatus.UNAUTHORIZED
                response.headers.contentType = MediaType.APPLICATION_JSON
                val content = response.bufferFactory()
                    .wrap(serialize((it as BaseException).errorResponse()).toByteArray())
                response.writeWith(createMono(content))
            }
            .contextWrite { it.put("startTime", startTime) }
            .contextWrite { it.put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange)) }
            .doFinally {
                val logDetails = LogDetails.create(
                    message = "Successfully send api response",
                    traceId = getTraceId(exchange),
                    requestDetails = RequestDetails(
                        method = exchange.request.method,
                        headers = exchange.request.headers,
                        uriWithParams = exchange.request.uri.toString(),
                        body = exchange.request.body.toString()
                    ),
                    responseDetails = ResponseDetails(
                        headers = exchange.response.headers,
                        statusCode = exchange.response.statusCode.toString(),
                        time = (LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) * 1000,
                        body = exchange.response.bufferFactory().toString()
                    ),
                    additionalDetails = additionalDetails
                )
                logger.info(logDetails)
            }
    }


    private fun authorize(exchange: ServerWebExchange): Mono<IAuthenticationData> {
        val authHeader = exchange.request.headers.getFirst(AUTHORIZATION)
        if (routeValidator.isUnsecured(exchange.request) || authHeader == appConfig.internalAccessToken) {
            return createMono(AuthUserData("00000000"))
        }
        val fullSecured = routeValidator.isSecured(exchange.request)
        return authGateway.validate(exchange)
            .flatMap { authenticationData ->
                if (fullSecured && authenticationData !is PremisesActorData) {
                    createMonoError(UnAuthorizedException(NexoraError.NEXORA0101))
                } else {
                    createMono(authenticationData)
                }
            }
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

