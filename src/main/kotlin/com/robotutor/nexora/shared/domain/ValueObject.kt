package com.robotutor.nexora.shared.domain

abstract class ValueObject {
    init {
        validate()
    }

    protected open fun validate() {}
}


