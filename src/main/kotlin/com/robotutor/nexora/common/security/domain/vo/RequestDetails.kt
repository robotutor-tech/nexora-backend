package com.robotutor.nexora.common.security.domain.vo

import org.springframework.http.HttpMethod

data class RequestDetails(val method: HttpMethod, val uri: String? = null)
