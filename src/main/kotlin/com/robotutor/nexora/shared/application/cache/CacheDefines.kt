package com.robotutor.nexora.shared.application.cache

/**
 * Application-layer cache namespace constants.
 *
 * Bounded Context application code can reference these names without importing
 * infrastructure packages from `common`.
 */
object CacheNames {
    const val USER_BY_ID = "user-by-id"
}

/**
 * Application-layer typed cache key builders.
 *
 * Keep these pure so use-cases can reference them in annotations without depending
 * on infrastructure packages.
 */
object CacheKeys {
    fun userById(principalId: String): String = "user-by-id:$principalId"
}

