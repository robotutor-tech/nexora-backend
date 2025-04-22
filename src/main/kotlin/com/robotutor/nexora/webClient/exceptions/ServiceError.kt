package com.robotutor.nexora.webClient.exceptions

interface ServiceError {
    val errorCode: String
    val message: String
}

fun ServiceError.toBaseException() = BaseException(this)
