package com.robotutor.nexora.module.seed.seed

import reactor.core.publisher.Mono

interface SeedData {
    val name: String
    fun execute(): Mono<Any>
}
