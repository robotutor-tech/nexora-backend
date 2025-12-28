package com.robotutor.nexora.shared.application.cache

object CacheNames {
    const val USER_BY_ID = "user-by-id"
}

object CacheKeys {
    fun userById(principalId: String): String = "user-by-id:$principalId"
}

