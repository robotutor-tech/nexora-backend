package com.robotutor.nexora.common.cache.service

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.domain.specification.Specification
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

fun <A : Aggregate> argumentIndexForSpecification(method: Method, aggregateClazz: Class<A>): Int {
    return if (method.parameters.size > 1) {
        method.parameters
            .indexOfFirst {
                it.type == Specification::class.java &&
                        it.parameterizedType is ParameterizedType &&
                        (it.parameterizedType as ParameterizedType).actualTypeArguments.first() == aggregateClazz
            }
    } else 0
}