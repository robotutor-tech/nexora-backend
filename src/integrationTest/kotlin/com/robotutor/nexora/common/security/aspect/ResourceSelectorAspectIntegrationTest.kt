package com.robotutor.nexora.common.security.aspect

import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@ContextConfiguration(classes = [ResourceSelectorAspectIntegrationTest.TestConfig::class])
class ResourceSelectorAspectIntegrationTest {

    data class Cmd(val deviceId: String)

    @Test
    fun `authorize allows access and method proceeds`() {
        val service = TestConfig.service

        StepVerifier.create(service.secured(Cmd("d1")))
            .assertNext { assertEquals("ok:d1", it) }
            .verifyComplete()
    }

    @Test
    fun `authorize denies access and returns unauthorized`() {
        val service = TestConfig.deniedService

        StepVerifier.create(service.secured(Cmd("d2")))
            .verifyError()
    }

    @Configuration
    class TestConfig {
        companion object {
            lateinit var service: SecuredService
            lateinit var deniedService: SecuredDeniedService
        }

        @Bean
        fun accessAuthorizer(): com.robotutor.nexora.common.security.ports.AccessAuthorizer = object :
            com.robotutor.nexora.common.security.ports.AccessAuthorizer {
            override fun authorize(httpAuthorize: HttpAuthorize, resourceId: ResourceId): Mono<Boolean> {
                return Mono.just(resourceId.value != "d2")
            }

            override fun getAuthorizedScope(exchange: org.springframework.web.server.ServerWebExchange, httpAuthorize: HttpAuthorize): Mono<com.robotutor.nexora.shared.interfaces.view.AuthorizedResources> {
                return Mono.error(UnsupportedOperationException("not needed"))
            }
        }

        @Bean
        fun securedService(): SecuredService {
            service = SecuredService()
            return service
        }

        @Bean
        fun securedDeniedService(): SecuredDeniedService {
            deniedService = SecuredDeniedService()
            return deniedService
        }
    }

    class SecuredService {
        @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
        fun secured(command: Cmd): Mono<String> = Mono.just("ok:${command.deviceId}")
    }

    class SecuredDeniedService {
        @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
        fun secured(command: Cmd): Mono<String> = Mono.just("ok:${command.deviceId}")
    }
}

