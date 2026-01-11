package com.robotutor.nexora.module.automation.domain.vo.component

import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface ConditionSpecification<C : Condition> : Specification<C>, Condition