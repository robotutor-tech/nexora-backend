package com.robotutor.nexora.module.automation.application.resolver

import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.shared.domain.specification.AndSpecification
import com.robotutor.nexora.shared.domain.specification.NotSpecification
import com.robotutor.nexora.shared.domain.specification.OrSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.utility.createFlux
import reactor.core.publisher.Mono

object SpecificationResolver {


    private fun <C : Condition, D : ComponentData<C>> resolveLeaf(specification: C): Mono<Specification<D>> {

    }
}