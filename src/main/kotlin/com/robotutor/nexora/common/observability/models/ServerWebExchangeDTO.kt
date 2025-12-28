package com.robotutor.nexora.common.observability.models

import org.springframework.http.HttpMethod

data class RequestDetails(val method: HttpMethod, val uri: String? = null)
data class ResponseDetails(val statusCode: String = "", val time: Long = -1)