package com.robotutor.nexora.webClient.controllers

import com.robotutor.nexora.webClient.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import kotlin.io.AccessDeniedException

@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(BadDataException::class)
    fun handleBadDataException(ex: BadDataException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.errorResponse())
    }

    @ExceptionHandler(UnAuthorizedException::class)
    fun handleUnAuthorizedException(ex: UnAuthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.errorResponse())
    }

    @ExceptionHandler(DataNotFoundException::class)
    fun handleDataNotFoundException(ex: DataNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.errorResponse())
    }

    @ExceptionHandler(DuplicateDataException::class)
    fun handleDuplicateDataException(ex: DuplicateDataException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.errorResponse())
    }

    @ExceptionHandler(TooManyRequestsException::class)
    fun handleException(ex: TooManyRequestsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.errorResponse())
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(ex: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { it.defaultMessage!! }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(errorCode = "NEXORA-0001", message = errors.first()))
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(errorCode = "NEXORA-0002", message = ex.reason ?: ""))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleNoHandlerFoundException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(ex.statusCode).body(
            ErrorResponse(
                errorCode = "NEXORA-0003",
                message = ex.message
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(errorCode = "NEXORA-0004", message = "Internal Server Error"))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(errorCode = "NEXORA-0005", message = ex.reason ?: ""))
    }

}