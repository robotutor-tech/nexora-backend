package com.robotutor.nexora.common.security.filters

import com.robotutor.nexora.common.security.config.AppConfig
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class RouteValidator(private val appConfig: AppConfig) {

    fun isUnsecured(request: ServerHttpRequest): Boolean {
        return appConfig.unSecuredPath.any { request.uri.path == it }
    }
}
