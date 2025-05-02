package com.robotutor.nexora.widget.repositories

import com.robotutor.nexora.widget.models.Widget
import com.robotutor.nexora.widget.models.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WidgetRepository : ReactiveCrudRepository<Widget, WidgetId> {

}
