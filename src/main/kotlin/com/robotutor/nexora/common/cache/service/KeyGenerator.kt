package com.robotutor.nexora.common.cache.service

import java.lang.reflect.Method

interface KeyGenerator {
    fun generate(method: Method, vararg args: Any): String
}