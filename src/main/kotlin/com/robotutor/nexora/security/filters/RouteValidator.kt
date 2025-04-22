package com.robotutor.nexora.security.filters

import com.robotutor.nexora.security.config.AppConfig
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class RouteValidator(private val appConfig: AppConfig) {

    fun isUnsecured(request: ServerHttpRequest): Boolean {
        return appConfig.unSecuredPath.any { request.uri.path == it }
    }

    fun isSecured(request: ServerHttpRequest): Boolean {
        return !isPartiallySecured(request)
    }

    fun isPartiallySecured(request: ServerHttpRequest): Boolean {
        return !isUnsecured(request) && appConfig.partiallySecuredPath.any { request.uri.path == it }
    }
}
