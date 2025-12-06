package com.robotutor.nexora.shared.domain.exception

interface ServiceError {
    val errorCode: String
    val message: String
}
