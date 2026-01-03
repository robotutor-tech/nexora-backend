package com.robotutor.nexora.common.cache.service

import java.lang.reflect.Method

class DefaultKeyGenerator : KeyGenerator {
    override fun generate(method: Method, vararg args: Any): String {
        return "${method.javaClass.name}:${method.name}:${args.joinToString(":")}"
    }
}