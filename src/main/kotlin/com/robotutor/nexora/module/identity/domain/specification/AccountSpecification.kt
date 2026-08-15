package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface AccountSpecification : Specification<Account>
