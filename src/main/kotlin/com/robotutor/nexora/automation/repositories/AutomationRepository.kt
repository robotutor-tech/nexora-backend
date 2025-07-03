package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.Automation
import com.robotutor.nexora.automation.models.AutomationId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AutomationRepository : ReactiveCrudRepository<Automation, AutomationId> {

}
