package com.robotutor.nexora.shared.infrastructure.serializer.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.robotutor.nexora.shared.domain.model.ActorPrincipalContext
import com.robotutor.nexora.shared.domain.model.ActorPrincipalData
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.PrincipalData
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import kotlin.jvm.java

@Configuration
class JacksonMixinsConfig(private val objectMapper: ObjectMapper) {
    @PostConstruct
    fun setUp() {
        objectMapper.addMixIn(PrincipalData::class.java, PrincipalDataMixin::class.java)
        objectMapper.addMixIn(ActorPrincipalData::class.java, ActorPrincipalDataMixin::class.java)
        objectMapper.addMixIn(ActorPrincipalContext::class.java, ActorPrincipalContextMixin::class.java)
        objectMapper.addMixIn(PrincipalContext::class.java, PrincipalContextMixin::class.java)
    }
}

