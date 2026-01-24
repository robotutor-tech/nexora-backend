package com.robotutor.nexora.module.automation.domain.specification

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface AutomationSpecification : Specification<AutomationAggregate>