package com.robotutor.nexora.shared.domain.vo

data class Tokens(val accessToken: AccessToken, val refreshToken: AccessToken) : ValueObject
