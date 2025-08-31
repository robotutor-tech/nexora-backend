package com.robotutor.nexora.shared.infrastructure.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.robotutor.nexora.shared.domain.model.ActorPrincipalContext
import org.bson.types.ObjectId
import org.springframework.http.HttpMethod
import com.robotutor.nexora.shared.infrastructure.serializer.jackson.PrincipalDataMixin
import com.robotutor.nexora.shared.infrastructure.serializer.jackson.ActorPrincipalDataMixin
import com.robotutor.nexora.shared.domain.model.PrincipalData
import com.robotutor.nexora.shared.domain.model.ActorPrincipalData
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.infrastructure.serializer.jackson.ActorPrincipalContextMixin
import com.robotutor.nexora.shared.infrastructure.serializer.jackson.PrincipalContextMixin

object ObjectMapperCache {
    val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModule(SimpleModule().apply {
            addSerializer(HttpMethod::class.java, HttpMethodSerializer())
            addSerializer(ObjectId::class.java, ObjectIdSerializer())
        })
        .registerKotlinModule()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .apply {
            addMixIn(PrincipalData::class.java, PrincipalDataMixin::class.java)
            addMixIn(ActorPrincipalData::class.java, ActorPrincipalDataMixin::class.java)
            addMixIn(ActorPrincipalContext::class.java, ActorPrincipalContextMixin::class.java)
            addMixIn(PrincipalContext::class.java, PrincipalContextMixin::class.java)
        }
}