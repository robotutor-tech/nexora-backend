package com.robotutor.nexora.common.observability.infrastructure.logger

import com.robotutor.nexora.shared.application.observability.AppLogger
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.common.observability.infrastructure.models.RequestDetails
import com.robotutor.nexora.common.observability.infrastructure.models.ResponseDetails
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class CommonAppLoggerFactory : AppLoggerFactory {
    override fun forClass(clazz: Class<*>): AppLogger {
        val delegate = Logger(clazz)
        return CommonAppLogger(delegate)
    }
}

private class CommonAppLogger(
    private val delegate: Logger,
) : AppLogger {

    override fun info(
        message: String,
        additionalDetails: Map<String, Any?>,
        searchableFields: Map<String, Any?>,
        skipAdditionalDetails: Boolean,
        skipResponseBody: Boolean,
    ) {
        delegate.info(
            LogDetails.create(
                message = message,
                traceId = "missing-trace-id",
                premisesId = "missing-premises-id",
                searchableFields = searchableFields,
                errorCode = null,
                requestDetails = RequestDetails(
                    method = HttpMethod.GET,
                    headers = HttpHeaders.EMPTY,
                    uriWithParams = null,
                    body = null,
                ),
                responseDetails = ResponseDetails(
                    headers = HttpHeaders.EMPTY,
                    statusCode = "",
                    time = -1,
                    body = null,
                ),
                additionalDetails = if (skipAdditionalDetails) emptyMap() else additionalDetails,
            )
        )
    }

    override fun error(
        errorMessage: String,
        throwable: Throwable?,
        errorCode: String?,
        additionalDetails: Map<String, Any?>,
        searchableFields: Map<String, Any?>,
        skipAdditionalDetails: Boolean,
    ) {
        val details = LogDetails(
            errorCode = errorCode,
            message = errorMessage,
            traceId = "missing-trace-id",
            premisesId = "missing-premises-id",
            additionalDetails = if (skipAdditionalDetails) emptyMap() else additionalDetails,
            searchableFields = searchableFields,
            responseTime = -1,
        )
        delegate.error(details, throwable ?: RuntimeException(errorMessage))
    }
}
