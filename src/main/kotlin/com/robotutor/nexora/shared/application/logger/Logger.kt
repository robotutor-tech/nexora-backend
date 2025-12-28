package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.common.serialization.infrastructure.DefaultSerializer.serialize
import org.slf4j.LoggerFactory

class Logger(className: Class<out Any>) {
    private val logger = LoggerFactory.getLogger(className)

    fun info(details: LogDetails) {
        logger.info(serialize(details))
    }

    fun error(details: LogDetails, exception: Throwable? = null) {
        logger.error(serialize(details), exception)
    }
}