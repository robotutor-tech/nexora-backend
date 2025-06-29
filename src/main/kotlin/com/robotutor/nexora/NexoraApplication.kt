 package com.robotutor.nexora

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan("com.robotutor.nexora")
@SpringBootApplication
class NexoraBackendApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(NexoraBackendApplication::class.java).run(*args)
        }
    }
}
