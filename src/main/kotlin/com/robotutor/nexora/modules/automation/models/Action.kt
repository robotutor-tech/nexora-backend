//package com.robotutor.nexora.modules.automation.models
//
//import com.robotutor.nexora.modules.feed.models.FeedId
//import com.robotutor.nexora.common.security.models.PremisesId
//import org.bson.types.ObjectId
//import java.time.Instant
//
//data class Action(
//    var id: ObjectId? = null,
//    val actionId: ActionId,
//    val premisesId: PremisesId,
//    val name: String,
//    val description: String? = null,
//    val config: ActionConfig,
//    val createdOn: Instant = Instant.now(),
//    val updatedOn: Instant = Instant.now(),
//    val version: Long? = null
//)
//
//enum class ActionType {
//    FEED_CONTROL,
//    WAIT,
//    AUTOMATION_TRIGGER,
//}
//
//sealed interface ActionConfig {
//    val type: ActionType
//}
//
//
//data class FeedControlActionConfig(
//    override val type: ActionType = ActionType.FEED_CONTROL,
//    val feedId: FeedId,
//    val value: Int
//) : ActionConfig
//
//data class WaitActionConfig(
//    override val type: ActionType = ActionType.WAIT,
//    val duration: Int
//) : ActionConfig
//
//data class AutomationActionConfig(
//    override val type: ActionType = ActionType.AUTOMATION_TRIGGER,
//    val automationId: AutomationId
//) : ActionConfig
//
//typealias ActionId = String
