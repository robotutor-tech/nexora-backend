package com.robotutor.nexora.automation.models

import com.robotutor.nexora.automation.controllers.views.ActionRequest
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ACTION_COLLECTION = "actions"

@TypeAlias("Action")
@Document(ACTION_COLLECTION)
data class Action(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val actionId: ActionId,
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val type: ActionType,
    val config: ActionConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(actionId: ActionId, request: ActionRequest, premisesActorData: PremisesActorData): Action {
            return Action(
                actionId = actionId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                description = request.description,
                type = request.type,
                config = request.config,
            )
        }
    }
}

enum class ActionType {
    FEED_CONTROL,
    DELAY,
    AUTOMATION_TRIGGER,
}

sealed interface ActionConfig

data class FeedControlActionConfig(val feedId: FeedId, val value: Number) : ActionConfig
data class DelayActionConfig(val durationInMinute: Int) : ActionConfig
data class AutomationActionConfig(val automationId: AutomationId) : ActionConfig

typealias ActionId = String
