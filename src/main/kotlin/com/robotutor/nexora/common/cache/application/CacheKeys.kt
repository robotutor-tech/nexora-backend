package com.robotutor.nexora.common.cache.application

object CacheKeys {
    fun userById(userId: String) = "user:$userId"
    fun deviceById(deviceId: String) = "device:$deviceId"
    fun accountById(accountId: String) = "account:$accountId"
}

