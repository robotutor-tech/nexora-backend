package com.robotutor.nexora.iam.repositories

import com.robotutor.nexora.iam.models.Policy
import com.robotutor.nexora.iam.models.PolicyId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PolicyRepository : ReactiveCrudRepository<Policy, PolicyId> {
}
