package com.robotutor.nexora.shared.infrastructure.webclient.controllers

import com.robotutor.nexora.shared.domain.exception.AccessDeniedException
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.ClientException
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.DuplicateDataException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.shared.domain.exception.ServerException
import com.robotutor.nexora.shared.domain.exception.TooManyRequestsException
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.logger.LogDetails
import com.robotutor.nexora.shared.logger.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class ApiExceptionHandler {
    val logger = Logger(this::class.java)

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
    fun handleTooManyRequestException(ex: TooManyRequestsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.errorResponse())
    }

    @ExceptionHandler(ClientException::class)
    fun handleClientException(ex: ClientException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.errorResponse())
    }


    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.errorResponse())
    }

    @ExceptionHandler(ServerException::class)
    fun handleServerException(ex: ServerException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.errorResponse())
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
        logger.error(LogDetails(message = "Internal server error"), ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(errorCode = "NEXORA-0004", message = "Internal Server Error"))
    }

}