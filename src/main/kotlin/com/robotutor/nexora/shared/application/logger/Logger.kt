package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer.serialize
import org.slf4j.LoggerFactory

class Logger(className: Class<out Any>) {
    private val logger = LoggerFactory.getLogger(className)

    fun debug(details: LogDetails) {
        logger.debug(serialize(details))
    }

    fun info(details: LogDetails) {
        logger.info(serialize(details))
    }

    fun error(details: LogDetails, exception: Throwable? = null) {
        logger.error(serialize(details), exception)
    }

    fun warn(details: LogDetails) {
        logger.warn(serialize(details))
    }

    fun log(details: LogDetails, level: LogLevel, exception: Throwable? = null) {
        when (level) {
            LogLevel.DEBUG -> debug(details)
            LogLevel.INFO -> info(details)
            LogLevel.WARN -> warn(details)
            LogLevel.ERROR -> error(details, exception)
        }
    }
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
}
