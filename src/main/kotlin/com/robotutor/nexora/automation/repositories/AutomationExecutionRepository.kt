package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.AutomationExecution
import com.robotutor.nexora.automation.models.ExecutionId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AutomationExecutionRepository : ReactiveCrudRepository<AutomationExecution, ExecutionId> {

}
