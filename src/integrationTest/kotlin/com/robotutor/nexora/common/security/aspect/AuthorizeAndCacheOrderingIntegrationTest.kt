package com.robotutor.nexora.common.security.aspect

import com.robotutor.nexora.common.cache.application.CacheNames
import com.robotutor.nexora.common.security.domain.vo.AuthorizedResources
import com.robotutor.nexora.common.security.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.cache.annotation.Cached
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@ContextConfiguration(classes = [AuthorizeAndCacheOrderingIntegrationTest.TestConfig::class])
class AuthorizeAndCacheOrderingIntegrationTest {

    data class Cmd(val deviceId: String)

    @Test
    fun `when unauthorized, cached value must not be served`() {
        val svc = TestConfig.deniedService

        // First call would populate cache if it proceeded; it must not.
        StepVerifier.create(svc.secured(Cmd("d2")))
            .verifyError()

        // Second call must still be unauthorized (and not succeed due to cache hit).
        StepVerifier.create(svc.secured(Cmd("d2")))
            .verifyError()
    }

    @Configuration
    class TestConfig {
        companion object {
            lateinit var deniedService: SecuredDeniedService
        }

        @Bean
        fun cacheManager(): CacheManager = ConcurrentMapCacheManager(CacheNames.DEVICE_BY_ID, CacheNames.USER_BY_ID)

        @Bean
        fun accessAuthorizer(): com.robotutor.nexora.common.security.ports.AccessAuthorizer = object :
            com.robotutor.nexora.common.security.ports.AccessAuthorizer {
            override fun authorize(httpAuthorize: HttpAuthorize, resourceId: ResourceId): Mono<Boolean> {
                // deny d2
                return Mono.just(resourceId.value != "d2")
            }

            override fun getAuthorizedScope(
                exchange: org.springframework.web.server.ServerWebExchange,
                httpAuthorize: HttpAuthorize
            ): Mono<AuthorizedResources> {
                return Mono.error(UnsupportedOperationException("not needed"))
            }
        }

        @Bean
        fun securedDeniedService(): SecuredDeniedService {
            deniedService = SecuredDeniedService()
            return deniedService
        }
    }

    class SecuredDeniedService {

        @Authorize(ActionType.READ, ResourceType.DEVICE, "#command.deviceId")
        @Cached(cacheName = CacheNames.DEVICE_BY_ID, key = "'device:' + #command.deviceId")
        fun secured(command: Cmd): Mono<String> = Mono.just("ok:${command.deviceId}")
    }
}

