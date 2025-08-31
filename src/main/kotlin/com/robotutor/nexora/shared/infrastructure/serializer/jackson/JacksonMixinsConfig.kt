//package com.robotutor.nexora.shared.infrastructure.serializer.jackson
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.robotutor.nexora.shared.domain.model.ActorPrincipalData
//import com.robotutor.nexora.shared.domain.model.PrincipalData
//import jakarta.annotation.PostConstruct
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//class JacksonMixinsConfig(private val objectMapper: ObjectMapper) {
//    @PostConstruct
//    fun setUp() {
//        objectMapper.addMixIn(PrincipalData::class.java, com.robotutor.nexora.shared.infrastructure.jackson.PrincipalDataMixin::class.java)
//        objectMapper.addMixIn(ActorPrincipalData::class.java, com.robotutor.nexora.shared.infrastructure.jackson.ActorPrincipalDataMixin::class.java)
//    }
//}
//
