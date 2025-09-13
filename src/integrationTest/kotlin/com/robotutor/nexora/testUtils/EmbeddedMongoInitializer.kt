package com.robotutor.nexora.testUtils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class EmbeddedMongoInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        // No-op: relying on de.flapdoodle embedded Mongo Spring autoconfiguration for tests
    }
}
