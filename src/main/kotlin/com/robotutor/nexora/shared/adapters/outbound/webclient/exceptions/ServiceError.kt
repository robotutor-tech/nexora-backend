package com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions

interface ServiceError {
    val errorCode: String
    val message: String
}

fun ServiceError.toBaseException() = BaseException(this)
