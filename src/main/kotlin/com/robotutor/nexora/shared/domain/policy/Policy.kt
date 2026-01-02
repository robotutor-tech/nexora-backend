package com.robotutor.nexora.shared.domain.policy

interface Policy<T> {
    fun evaluate(input: T): PolicyResult
}
