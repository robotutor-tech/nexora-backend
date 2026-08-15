package com.robotutor.nexora.module.identity.interfaces.controller.view

data class RegisterUserAccountRequest(
    val email: String,
    val password: String,
    val userId: String
)
