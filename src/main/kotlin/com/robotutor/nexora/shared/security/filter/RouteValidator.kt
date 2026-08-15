package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.security.config.AppConfig
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

const val REFRESH_TOKEN_HEADER = "refreshToken"
const val BEARER_PREFIX = "Bearer "

@Component
class RouteValidator(private val appConfig: AppConfig) {

    fun isUnsecured(request: ServerHttpRequest): Boolean {
        return appConfig.unSecuredPath.any { request.uri.path == it }
    }

    fun getToken(request: ServerHttpRequest): String? {
        val isRefreshPath = request.uri.path.equals(appConfig.refreshPath)
        val headerName = if (isRefreshPath) REFRESH_TOKEN_HEADER else HttpHeaders.AUTHORIZATION
        return request.headers.getFirst(headerName)?.removePrefix(BEARER_PREFIX)
    }
}
