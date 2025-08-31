package com.robotutor.nexora.shared.infrastructure.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.robotutor.nexora.shared.domain.model.ActorPrincipalData
import com.robotutor.nexora.shared.domain.model.PrincipalData
import com.robotutor.nexora.shared.infrastructure.serializer.jackson.ActorPrincipalDataMixin
import com.robotutor.nexora.shared.infrastructure.serializer.jackson.PrincipalDataMixin
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonMixinsConfig(private val objectMapper: ObjectMapper) {
    @PostConstruct
    fun setUp() {
        objectMapper.addMixIn(PrincipalData::class.java, PrincipalDataMixin::class.java)
        objectMapper.addMixIn(ActorPrincipalData::class.java, ActorPrincipalDataMixin::class.java)
    }
}
