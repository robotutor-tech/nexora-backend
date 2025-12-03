package com.robotutor.nexora.shared.domain.exception

data class PolicyViolationException(
    val errorCode: String,
    override val message: String,
) : BaseException(errorCode, message)