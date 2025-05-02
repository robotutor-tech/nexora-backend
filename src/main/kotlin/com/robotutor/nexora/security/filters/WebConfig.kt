package com.robotutor.nexora.security.filters

import com.robotutor.nexora.security.config.AppConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping

@EnableWebFluxSecurity
@Configuration
class WebConfig(private val appConfig: AppConfig) {
    @Bean
    fun handlerMapping(): RequestMappingHandlerMapping {
        return RequestMappingHandlerMapping()
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .build()
    }
}