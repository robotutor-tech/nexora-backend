package com.robotutor.nexora.common.security.controllers

import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

@Component
class ExceptionHandlerRegistry(private val apiExceptionHandler: ApiExceptionHandler) {

    private val handlers: Map<KClass<out Throwable>, KFunction<*>> = apiExceptionHandler::class.functions
        .filter { it.annotations.any { ann -> ann is ExceptionHandler } }
        .associateBy { function ->
            function.annotations.filterIsInstance<ExceptionHandler>().first().value.first()
        }

    fun handle(ex: Throwable): ResponseEntity<ErrorResponse> {
        val matchedHandler = handlers.entries.find { (exceptionType, _) ->
            exceptionType.isInstance(ex)
        } ?: return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("INTERNAL_ERROR", ex.message ?: "Internal Error"))

        val method = matchedHandler.value

        @Suppress("UNCHECKED_CAST")
        return method.call(apiExceptionHandler, ex) as ResponseEntity<ErrorResponse>
    }
}