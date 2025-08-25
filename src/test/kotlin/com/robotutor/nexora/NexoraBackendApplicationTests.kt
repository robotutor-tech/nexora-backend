package com.robotutor.nexora

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono

@SpringBootTest
class NexoraBackendApplicationTests {

    @Test
    fun contextLoads() {
        Mono.just("data")
            .subscribe()
    }

}
