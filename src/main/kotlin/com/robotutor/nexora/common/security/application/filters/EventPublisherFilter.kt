package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.shared.domain.event.EventPublisher
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class EventPublisherFilter(private val eventPublisher: EventPublisher) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(exchange)
            .contextWrite { it.put(EventPublisher::class.java, eventPublisher) }
    }
}